package org.example.service;

import org.example.factory.BookFactory;
import org.example.model.AbstractBook;
import org.example.model.PhysicalBook;
import org.example.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// testy dla BookService z uzyciem mockow
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookFactory bookFactory;

    @InjectMocks
    private BookService bookService;

    private AbstractBook testBook;

    @BeforeEach
    void setUp() {
        testBook = new PhysicalBook("Test Book", "Test Author", 3, 3);
        testBook.setId(1L);
        testBook.setGenre("Fiction");
        testBook.setPublicationYear(2023);
    }

    @Test
    void shouldGetAllBooks() {
        // given
        List<AbstractBook> books = Arrays.asList(testBook);
        when(bookRepository.findAll()).thenReturn(books);

        // when
        List<AbstractBook> allBooks = bookService.getAllBooks();

        // then
        assertThat(allBooks).hasSize(1);
        assertThat(allBooks.get(0).getTitle()).isEqualTo("Test Book");
    }

    @Test
    void shouldGetBookById() {
        // given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // when
        Optional<AbstractBook> foundBook = bookService.getBookById(1L);

        // then
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("Test Book");
    }

    @Test
    void shouldCreateBook() {
        // given
        when(bookFactory.createBook(anyString(), anyString(), anyString(), any(), any()))
                .thenReturn(testBook);
        when(bookRepository.save(any(AbstractBook.class))).thenReturn(testBook);

        // when
        AbstractBook createdBook = bookService.createBook(
                "Test Book", "Test Author", 2023, "Fiction", 3, "PHYSICAL"
        );

        // then
        assertThat(createdBook.getTitle()).isEqualTo("Test Book");
        verify(bookRepository).save(any(AbstractBook.class));
    }

    @Test
    void shouldUpdateBook() {
        // given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(AbstractBook.class))).thenReturn(testBook);

        // when
        AbstractBook updatedBook = bookService.updateBook(
                1L, "Updated Title", "Updated Author", 2024, "Updated Genre", 5
        );

        // then
        assertThat(updatedBook.getTitle()).isEqualTo("Updated Title");
        verify(bookRepository).save(testBook);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentBook() {
        // given
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookService.updateBook(
                999L, "Title", "Author", 2023, "Genre", 3
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Book not found: 999");
    }

    @Test
    void shouldDeleteBook() {
        // given
        when(bookRepository.existsById(1L)).thenReturn(true);

        // when
        bookService.deleteBook(1L);

        // then
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentBook() {
        // given
        when(bookRepository.existsById(999L)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> bookService.deleteBook(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book not found: 999");
    }

    @Test
    void shouldSearchBooks() {
        // given
        List<AbstractBook> books = Arrays.asList(testBook);
        when(bookRepository.searchBooks("test")).thenReturn(books);

        // when
        List<AbstractBook> searchResults = bookService.searchBooks("test");

        // then
        assertThat(searchResults).hasSize(1);
        assertThat(searchResults.get(0).getTitle()).isEqualTo("Test Book");
    }

    @Test
    void shouldReturnAllBooksWhenSearchTermEmpty() {
        // given
        List<AbstractBook> books = Arrays.asList(testBook);
        when(bookRepository.findAll()).thenReturn(books);

        // when
        List<AbstractBook> searchResults = bookService.searchBooks("");

        // then
        assertThat(searchResults).hasSize(1);
        verify(bookRepository).findAll();
    }

    @Test
    void shouldGetBooksByGenre() {
        // given
        List<AbstractBook> books = Arrays.asList(testBook);
        when(bookRepository.findByGenreIgnoreCase("Fiction")).thenReturn(books);

        // when
        List<AbstractBook> genreBooks = bookService.getBooksByGenre("Fiction");

        // then
        assertThat(genreBooks).hasSize(1);
        assertThat(genreBooks.get(0).getGenre()).isEqualTo("Fiction");
    }

    @Test
    void shouldGetAvailableBooks() {
        // given
        List<AbstractBook> books = Arrays.asList(testBook);
        when(bookRepository.findByAvailableCopiesGreaterThan(0)).thenReturn(books);

        // when
        List<AbstractBook> availableBooks = bookService.getAvailableBooks();

        // then
        assertThat(availableBooks).hasSize(1);
    }

    @Test
    void shouldCheckIfBookIsAvailable() {
        // given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // when
        boolean isAvailable = bookService.isBookAvailable(1L);

        // then
        assertThat(isAvailable).isTrue();
    }
}