package org.example.controller;

import org.example.model.AbstractBook;
import org.example.model.EBook;
import org.example.model.PhysicalBook;
import org.example.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolyBookController.class)
@Import(org.example.config.SecurityConfig.class)
@ActiveProfiles("test")
class PolyBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    private AbstractBook physicalBook;
    private AbstractBook eBook;

    @BeforeEach
    void setUp() {
        physicalBook = new PhysicalBook("Physical Book", "Physical Author", 3, 3);
        physicalBook.setId(1L);
        physicalBook.setGenre("Fiction");
        physicalBook.setPublicationYear(2023);

        eBook = new EBook("Digital Book", "Digital Author", 5, 5);
        eBook.setId(2L);
        eBook.setGenre("Technology");
        eBook.setPublicationYear(2024);
    }

    @Test
    @WithMockUser
    void shouldGetBooksWithDisplayInfo() throws Exception {
        // given
        List<AbstractBook> books = Arrays.asList(physicalBook, eBook);
        when(bookService.getAllBooks()).thenReturn(books);

        // when & then
        mockMvc.perform(get("/api/poly/books/display"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Physical Book"))
                .andExpect(jsonPath("$[0].type").value("Physical book"))
                .andExpect(jsonPath("$[0].isDigital").value(false))
                .andExpect(jsonPath("$[1].title").value("Digital Book"))
                .andExpect(jsonPath("$[1].type").value("eBook"))
                .andExpect(jsonPath("$[1].isDigital").value(true));
    }

    @Test
    @WithMockUser
    void shouldGetBookStats() throws Exception {
        // given
        List<AbstractBook> books = Arrays.asList(physicalBook, eBook);
        when(bookService.getAllBooks()).thenReturn(books);

        // when & then
        mockMvc.perform(get("/api/poly/books/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBooks").value(2))
                .andExpect(jsonPath("$.digitalBooks").value(1))
                .andExpect(jsonPath("$.physicalBooks").value(1));
    }

    @Test
    @WithMockUser
    void shouldGetBooksByDigitalFilter() throws Exception {
        // given
        List<AbstractBook> books = Arrays.asList(physicalBook, eBook);
        when(bookService.getAllBooks()).thenReturn(books);

        // when & then - filter for digital books
        mockMvc.perform(get("/api/poly/books/filter")
                        .param("digital", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Digital Book"));

        // when & then - filter for physical books
        mockMvc.perform(get("/api/poly/books/filter")
                        .param("digital", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Physical Book"));
    }
}