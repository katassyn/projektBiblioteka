package org.example.repository;

import org.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// testy dla BorrowingRepository
@DataJpaTest
@ActiveProfiles("test")
class BorrowingRepositoryTest {

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;
    private AbstractBook testBook;
    private Borrowing testBorrowing;

    @BeforeEach
    void setUp() {
        // przygotowanie danych testowych
        testUser = new User("testuser", "password", "test@example.com", Role.USER);
        testUser = entityManager.persistAndFlush(testUser);

        testBook = new PhysicalBook("Test Book", "Test Author", 2, 3);
        testBook = entityManager.persistAndFlush(testBook);

        testBorrowing = new Borrowing(
                testUser,
                testBook,
                BorrowingStatus.BORROWED,
                LocalDate.now(),
                LocalDate.now().plusDays(14)
        );
    }

    @Test
    void shouldSaveAndFindBorrowing() {
        // given
        Borrowing savedBorrowing = borrowingRepository.save(testBorrowing);

        // when
        Optional<Borrowing> foundBorrowing = borrowingRepository.findById(savedBorrowing.getId());

        // then
        assertThat(foundBorrowing).isPresent();
        assertThat(foundBorrowing.get().getUser().getUsername()).isEqualTo("testuser");
        assertThat(foundBorrowing.get().getBook().getTitle()).isEqualTo("Test Book");
    }

    @Test
    void shouldFindByUser() {
        // given
        borrowingRepository.save(testBorrowing);

        // when
        List<Borrowing> borrowings = borrowingRepository.findByUser(testUser);

        // then
        assertThat(borrowings).hasSize(1);
        assertThat(borrowings.get(0).getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void shouldFindByUserAndStatus() {
        // given
        borrowingRepository.save(testBorrowing);

        // when
        List<Borrowing> borrowings = borrowingRepository.findByUserAndStatus(testUser, BorrowingStatus.BORROWED);

        // then
        assertThat(borrowings).hasSize(1);
        assertThat(borrowings.get(0).getStatus()).isEqualTo(BorrowingStatus.BORROWED);
    }

    @Test
    void shouldFindByStatus() {
        // given
        borrowingRepository.save(testBorrowing);

        // when
        List<Borrowing> borrowings = borrowingRepository.findByStatus(BorrowingStatus.BORROWED);

        // then
        assertThat(borrowings).hasSize(1);
        assertThat(borrowings.get(0).getStatus()).isEqualTo(BorrowingStatus.BORROWED);
    }

    @Test
    void shouldFindActiveBorrowing() {
        // given
        borrowingRepository.save(testBorrowing);

        // when
        Optional<Borrowing> activeBorrowing = borrowingRepository
                .findActiveBorrowingByUserAndBook(testUser.getId(), testBook.getId());

        // then
        assertThat(activeBorrowing).isPresent();
        assertThat(activeBorrowing.get().getStatus()).isEqualTo(BorrowingStatus.BORROWED);
    }

    @Test
    void shouldCheckIfActiveBorrowingExists() {
        // given
        borrowingRepository.save(testBorrowing);

        // when
        boolean exists = borrowingRepository
                .existsActiveBorrowingByUserAndBook(testUser.getId(), testBook.getId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldFindOverdueBorrowings() {
        // given - wypozyczenie przeterminowane
        testBorrowing.setDueDate(LocalDate.now().minusDays(1));
        borrowingRepository.save(testBorrowing);

        // when
        List<Borrowing> overdueBorrowings = borrowingRepository
                .findOverdueBorrowings(LocalDate.now());

        // then
        assertThat(overdueBorrowings).hasSize(1);
        assertThat(overdueBorrowings.get(0).getDueDate()).isBefore(LocalDate.now());
    }

    @Test
    void shouldReturnEmptyWhenNoActiveBorrowing() {
        // when
        Optional<Borrowing> activeBorrowing = borrowingRepository
                .findActiveBorrowingByUserAndBook(999L, 999L);

        // then
        assertThat(activeBorrowing).isEmpty();
    }
}