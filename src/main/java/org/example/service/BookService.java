package org.example.service;

import org.example.model.AbstractBook;
import org.example.model.BookType;
import org.example.factory.BookFactory;
import org.example.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// klasa serwisowa dla operacji na tabeli ksiazek
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookFactory bookFactory;

    @Autowired
    public BookService(BookRepository bookRepository, BookFactory bookFactory) {
        this.bookRepository = bookRepository;
        this.bookFactory = bookFactory;
    }

    // zwraca liste wszystkich ksiazek
    public List<AbstractBook> getAllBooks() {
        return bookRepository.findAll();
    }

    // znajduje ksiazke po ID
    public Optional<AbstractBook> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    // tworzy nowa ksiazke (tylko admin)
    @Transactional
    public AbstractBook createBook(String title, String author, Integer publicationYear, 
                          String genre, Integer totalCopies, String bookType) {
        AbstractBook book = bookFactory.createBook(bookType, title, author, totalCopies, totalCopies);
        book.setPublicationYear(publicationYear);
        book.setGenre(genre);
        return bookRepository.save(book);
    }

    // aktualizuje istniejaca ksiazke (tylko admin)
    @Transactional
    public AbstractBook updateBook(Long id, String title, String author, Integer publicationYear, 
                          String genre, Integer totalCopies) {
        AbstractBook book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));

        book.setTitle(title);
        book.setAuthor(author);
        book.setPublicationYear(publicationYear);
        book.setGenre(genre);
        book.setTotalCopies(totalCopies);
        
        // aktualizuj dostepne kopie jesli zmienila sie calkowita liczba
        int borrowedCopies = book.getTotalCopies() - book.getAvailableCopies();
        book.setAvailableCopies(Math.max(0, totalCopies - borrowedCopies));

        return bookRepository.save(book);
    }

    // usuwa ksiazke (tylko admin)
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Book not found: " + id);
        }
        bookRepository.deleteById(id);
    }

    // wyszukuje ksiazki wedlug roznych kryteriow
    public List<AbstractBook> searchBooks(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllBooks();
        }
        return bookRepository.searchBooks(searchTerm.trim());
    }

    // zwraca ksiazki po gatunku
    public List<AbstractBook> getBooksByGenre(String genre) {
        return bookRepository.findByGenreIgnoreCase(genre);
    }

    // zwraca tylko dostepne ksiazki
    public List<AbstractBook> getAvailableBooks() {
        return bookRepository.findByAvailableCopiesGreaterThan(0);
    }

    // sprawdza czy ksiazka jest dostepna do wypozyczenia
    public boolean isBookAvailable(Long bookId) {
        return bookRepository.findById(bookId)
                .map(book -> book.getAvailableCopies() > 0)
                .orElse(false);
    }
}