package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dataTransfer.BookRequest;
import org.example.model.PhysicalBook;
import org.example.model.AbstractBook;
import org.example.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// testy integracyjne dla BookController
@WebMvcTest(BookController.class)
@Import(org.example.config.SecurityConfig.class)
@ActiveProfiles("test")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private AbstractBook testBook;
    private BookRequest bookRequest;

    @BeforeEach
    void setUp() {
        testBook = new PhysicalBook("Test Book", "Test Author", 3, 3);
        testBook.setId(1L);
        testBook.setGenre("Fiction");
        testBook.setPublicationYear(2023);

        bookRequest = new BookRequest();
        bookRequest.setTitle("Test Book");
        bookRequest.setAuthor("Test Author");
        bookRequest.setGenre("Fiction");
        bookRequest.setPublicationYear(2023);
        bookRequest.setTotalCopies(3);
        bookRequest.setBookType("PHYSICAL");
    }

    @Test
    @WithMockUser
    void shouldGetAllBooks() throws Exception {
        // given
        List<AbstractBook> books = Arrays.asList(testBook);
        when(bookService.getAllBooks()).thenReturn(books);

        // when & then
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Test Book"));
    }

    @Test
    @WithMockUser
    void shouldGetBookById() throws Exception {
        // given
        when(bookService.getBookById(1L)).thenReturn(Optional.of(testBook));

        // when & then
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"));
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenBookDoesNotExist() throws Exception {
        // given
        when(bookService.getBookById(999L)).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateBook() throws Exception {
        // given
        when(bookService.createBook(anyString(), anyString(), any(), anyString(), any(), anyString()))
                .thenReturn(testBook);

        // when & then
        mockMvc.perform(post("/api/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));

        verify(bookService).createBook("Test Book", "Test Author", 2023, "Fiction", 3, "PHYSICAL");
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldForbidCreateBookForRegularUser() throws Exception {
        // when & then
        mockMvc.perform(post("/api/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateBook() throws Exception {
        // given
        when(bookService.updateBook(eq(1L), anyString(), anyString(), any(), anyString(), any()))
                .thenReturn(testBook);

        // when & then
        mockMvc.perform(put("/api/books/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteBook() throws Exception {
        // given
        doNothing().when(bookService).deleteBook(1L);

        // when & then
        mockMvc.perform(delete("/api/books/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book deleted successfully"));

        verify(bookService).deleteBook(1L);
    }

    @Test
    @WithMockUser
    void shouldSearchBooks() throws Exception {
        // given
        List<AbstractBook> books = Arrays.asList(testBook);
        when(bookService.searchBooks("test")).thenReturn(books);

        // when & then
        mockMvc.perform(get("/api/books/search")
                        .param("q", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"));
    }

    @Test
    @WithMockUser
    void shouldGetBooksByGenre() throws Exception {
        // given
        List<AbstractBook> books = Arrays.asList(testBook);
        when(bookService.getBooksByGenre("Fiction")).thenReturn(books);

        // when & then
        mockMvc.perform(get("/api/books/genre/Fiction"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].genre").value("Fiction"));
    }

    @Test
    @WithMockUser
    void shouldGetAvailableBooks() throws Exception {
        // given
        List<AbstractBook> books = Arrays.asList(testBook);
        when(bookService.getAvailableBooks()).thenReturn(books);

        // when & then
        mockMvc.perform(get("/api/books/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"));
    }
}
