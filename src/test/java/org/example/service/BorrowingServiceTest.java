package org.example.service;

import org.example.model.*;
import org.example.repository.BookRepository;
import org.example.repository.BorrowingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// testy dla BorrowingService z uzyciem mockow
@ExtendWith(MockitoExtension.class)
class BorrowingServiceTest {

    @Mock
    private BorrowingRepository borrowingRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private BorrowingService borrowingService;

    private User testUser;
    private AbstractBook testBook;
    private Borrowing testBorrowing;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "password", "test@example.com", Role.USER);
        testUser.setId(1L);

        testBook = new PhysicalBook("Test Book", "Test Author", 3, 3);
        testBook.setId(1L);

        testBorrowing = new Borrowing(
                testUser,
                testBook,
                BorrowingStatus.BORROWED,
                LocalDate.now(),
                LocalDate.now().plusDays(14)
        );
        testBorrowing.setId(1L);
    }

    @Test
    void shouldBorrowBook() {
        // given
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(borrowingRepository.existsActiveBorrowingByUserAndBook(1L, 1L)).thenReturn(false);
        when(borrowingRepository.save(any(Borrowing.class))).thenReturn(testBorrowing);

        // when
        Borrowing borrowing = borrowingService.borrowBook(1L);

        // then
        assertThat(borrowing.getUser().getId()).isEqualTo(1L);
        assertThat(borrowing.getBook().getId()).isEqualTo(1L);
        assertThat(borrowing.getStatus()).isEqualTo(BorrowingStatus.BORROWED);
        verify(bookRepository).save(testBook);
        verify(borrowingRepository).save(any(Borrowing.class));
    }

    @Test
    void shouldThrowExceptionWhenBookNotFound() {
        // given
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> borrowingService.borrowBook(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book not found");
    }

    @Test
    void shouldThrowExceptionWhenBookNotAvailable() {
        // given
        testBook.setAvailableCopies(0);
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // when & then
        assertThatThrownBy(() -> borrowingService.borrowBook(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book is not available");
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyBorrowedBook() {
        // given
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(borrowingRepository.existsActiveBorrowingByUserAndBook(1L, 1L)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> borrowingService.borrowBook(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("You already have this book borrowed or reserved");
    }

    @Test
    void shouldReturnBook() {
        // given
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(borrowingRepository.findById(1L)).thenReturn(Optional.of(testBorrowing));
        when(borrowingRepository.save(any(Borrowing.class))).thenReturn(testBorrowing);

        // when
        Borrowing returnedBorrowing = borrowingService.returnBook(1L);

        // then
        assertThat(returnedBorrowing.getStatus()).isEqualTo(BorrowingStatus.RETURNED);
        assertThat(returnedBorrowing.getReturnDate()).isEqualTo(LocalDate.now());
        verify(bookRepository).save(testBook);
    }

    @Test
    void shouldThrowExceptionWhenBorrowingNotFound() {
        // given
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(borrowingRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> borrowingService.returnBook(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Borrowing not found");
    }

    @Test
    void shouldThrowExceptionWhenBorrowingNotOwnedByUser() {
        // given
        User anotherUser = new User("another", "pass", "another@test.com", Role.USER);
        anotherUser.setId(2L);
        testBorrowing.setUser(anotherUser);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(borrowingRepository.findById(1L)).thenReturn(Optional.of(testBorrowing));

        // when & then
        assertThatThrownBy(() -> borrowingService.returnBook(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("This borrowing does not belong to you");
    }

    @Test
    void shouldGetUserBorrowingHistory() {
        // given
        List<Borrowing> borrowings = Arrays.asList(testBorrowing);
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(borrowingRepository.findByUser(testUser)).thenReturn(borrowings);

        // when
        List<Borrowing> history = borrowingService.getUserBorrowingHistory();

        // then
        assertThat(history).hasSize(1);
        assertThat(history.get(0).getUser().getId()).isEqualTo(1L);
    }

    @Test
    void shouldGetUserActiveBorrowings() {
        // given
        List<Borrowing> borrowings = Arrays.asList(testBorrowing);
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(borrowingRepository.findByUserAndStatus(testUser, BorrowingStatus.BORROWED))
                .thenReturn(borrowings);

        // when
        List<Borrowing> activeBorrowings = borrowingService.getUserActiveBorrowings();

        // then
        assertThat(activeBorrowings).hasSize(1);
        assertThat(activeBorrowings.get(0).getStatus()).isEqualTo(BorrowingStatus.BORROWED);
    }

    @Test
    void shouldGetAllBorrowings() {
        // given
        List<Borrowing> borrowings = Arrays.asList(testBorrowing);
        when(borrowingRepository.findAll()).thenReturn(borrowings);

        // when
        List<Borrowing> allBorrowings = borrowingService.getAllBorrowings();

        // then
        assertThat(allBorrowings).hasSize(1);
    }

    @Test
    void shouldUpdateOverdueBorrowings() {
        // given
        testBorrowing.setDueDate(LocalDate.now().minusDays(1));
        List<Borrowing> overdueBorrowings = Arrays.asList(testBorrowing);
        when(borrowingRepository.findOverdueBorrowings(any(LocalDate.class)))
                .thenReturn(overdueBorrowings);

        // when
        borrowingService.updateOverdueBorrowings();

        // then
        verify(borrowingRepository).save(testBorrowing);
        assertThat(testBorrowing.getStatus()).isEqualTo(BorrowingStatus.OVERDUE);
    }
}