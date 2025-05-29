package org.example.repository;

import org.example.model.PhysicalBook;
import org.example.model.EBook;
import org.example.model.AbstractBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// testy dla BookRepository
@DataJpaTest
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    private AbstractBook physicalBook;
    private AbstractBook eBook;

    @BeforeEach
    void setUp() {
        // przygotowanie danych testowych
        physicalBook = new PhysicalBook("Test Book", "Test Author", 3, 3);
        physicalBook.setGenre("Fiction");
        physicalBook.setPublicationYear(2023);

        eBook = new EBook("Digital Book", "Digital Author", 5, 5);
        eBook.setGenre("Technology");
        eBook.setPublicationYear(2024);
    }

    @Test
    void shouldSaveAndFindBook() {
        // given
        AbstractBook savedBook = bookRepository.save(physicalBook);

        // when
        var foundBook = bookRepository.findById(savedBook.getId());

        // then
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("Test Book");
        assertThat(foundBook.get().getAuthor()).isEqualTo("Test Author");
    }

    @Test
    void shouldFindByTitleContaining() {
        // given
        bookRepository.save(physicalBook);
        bookRepository.save(eBook);

        // when
        List<AbstractBook> books = bookRepository.findByTitleContainingIgnoreCase("test");

        // then
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Test Book");
    }

    @Test
    void shouldFindByAuthorContaining() {
        // given
        bookRepository.save(physicalBook);
        bookRepository.save(eBook);

        // when
        List<AbstractBook> books = bookRepository.findByAuthorContainingIgnoreCase("digital");

        // then
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getAuthor()).isEqualTo("Digital Author");
    }

    @Test
    void shouldFindByGenre() {
        // given
        bookRepository.save(physicalBook);
        bookRepository.save(eBook);

        // when
        List<AbstractBook> books = bookRepository.findByGenreIgnoreCase("fiction");

        // then
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getGenre()).isEqualTo("Fiction");
    }

    @Test
    void shouldFindAvailableBooks() {
        // given
        physicalBook.setAvailableCopies(0); // nie dostepna
        eBook.setAvailableCopies(2); // dostepna
        bookRepository.save(physicalBook);
        bookRepository.save(eBook);

        // when
        List<AbstractBook> availableBooks = bookRepository.findByAvailableCopiesGreaterThan(0);

        // then
        assertThat(availableBooks).hasSize(1);
        assertThat(availableBooks.get(0).getTitle()).isEqualTo("Digital Book");
    }

    @Test
    void shouldSearchBooks() {
        // given
        bookRepository.save(physicalBook);
        bookRepository.save(eBook);

        // when
        List<AbstractBook> books = bookRepository.searchBooks("test");

        // then
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Test Book");
    }

    @Test
    void shouldReturnEmptyListWhenNoMatch() {
        // given
        bookRepository.save(physicalBook);

        // when
        List<AbstractBook> books = bookRepository.findByTitleContainingIgnoreCase("nonexistent");

        // then
        assertThat(books).isEmpty();
    }
}