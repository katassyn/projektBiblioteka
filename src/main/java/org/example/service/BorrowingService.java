package org.example.service;

import org.example.model.*;
import org.example.repository.BookRepository;
import org.example.repository.BorrowingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// klasa serwisowa dla operacji na wypozyczeniach
@Service
public class BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final BookRepository bookRepository;
    private final UserService userService;

    @Autowired
    public BorrowingService(BorrowingRepository borrowingRepository, 
                           BookRepository bookRepository, 
                           UserService userService) {
        this.borrowingRepository = borrowingRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
    }

    // wypozycza ksiazke
    @Transactional
    public Borrowing borrowBook(Long bookId) {
        User currentUser = userService.getCurrentUser();
        
        AbstractBook book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        // sprawdz czy ksiazka jest dostepna
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalArgumentException("Book is not available");
        }

        // sprawdz czy uzytkownik nie ma juz wypozyczonej tej ksiazki
        if (borrowingRepository.existsActiveBorrowingByUserAndBook(currentUser.getId(), bookId)) {
            throw new IllegalArgumentException("You already have this book borrowed or reserved");
        }

        // zmniejsz dostepne kopie
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // stworz wypozyczenie
        Borrowing borrowing = new Borrowing(
                currentUser,
                book,
                BorrowingStatus.BORROWED,
                LocalDate.now(),
                LocalDate.now().plusDays(14) // 2 tygodnie na zwrot
        );

        return borrowingRepository.save(borrowing);
    }

    // zwraca ksiazke
    @Transactional
    public Borrowing returnBook(Long borrowingId) {
        User currentUser = userService.getCurrentUser();
        
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new IllegalArgumentException("Borrowing not found"));

        // sprawdz czy wypozyczenie nalezy do aktualnego uzytkownika
        if (!borrowing.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("This borrowing does not belong to you");
        }

        // sprawdz czy ksiazka nie jest juz zwrocona
        if (borrowing.getStatus() == BorrowingStatus.RETURNED) {
            throw new IllegalArgumentException("Book is already returned");
        }

        // ustaw status na zwrocona
        borrowing.setStatus(BorrowingStatus.RETURNED);
        borrowing.setReturnDate(LocalDate.now());

        // zwieksz dostepne kopie
        AbstractBook book = borrowing.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return borrowingRepository.save(borrowing);
    }

    // zwraca historie wypozyczen aktualnego uzytkownika
    public List<Borrowing> getUserBorrowingHistory() {
        User currentUser = userService.getCurrentUser();
        return borrowingRepository.findByUser(currentUser);
    }

    // zwraca aktywne wypozyczenia aktualnego uzytkownika
    public List<Borrowing> getUserActiveBorrowings() {
        User currentUser = userService.getCurrentUser();
        return borrowingRepository.findByUserAndStatus(currentUser, BorrowingStatus.BORROWED);
    }

    // zwraca wszystkie wypozyczenia (tylko admin)
    public List<Borrowing> getAllBorrowings() {
        return borrowingRepository.findAll();
    }

    // zwraca wypozyczenie po ID
    public Optional<Borrowing> getBorrowingById(Long id) {
        return borrowingRepository.findById(id);
    }

    // aktualizuje przeterminowane wypozyczenia
    @Transactional
    public void updateOverdueBorrowings() {
        List<Borrowing> overdueBorrowings = borrowingRepository.findOverdueBorrowings(LocalDate.now());
        
        for (Borrowing borrowing : overdueBorrowings) {
            borrowing.setStatus(BorrowingStatus.OVERDUE);
            borrowingRepository.save(borrowing);
        }
    }

    // zwraca przeterminowane wypozyczenia
    public List<Borrowing> getOverdueBorrowings() {
        return borrowingRepository.findByStatus(BorrowingStatus.OVERDUE);
    }
}