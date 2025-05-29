package org.example.controller;

import org.example.model.*;
import org.example.service.BorrowingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// testy integracyjne dla BorrowingController
@WebMvcTest(BorrowingController.class)
@Import(org.example.config.SecurityConfig.class)
@ActiveProfiles("test")
class BorrowingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BorrowingService borrowingService;

    private Borrowing testBorrowing;
    private User testUser;
    private AbstractBook testBook;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "password", "test@example.com", Role.USER);
        testUser.setId(1L);

        testBook = new PhysicalBook("Test Book", "Test Author", 2, 3);
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
    @WithMockUser
    void shouldBorrowBook() throws Exception {
        // given
        when(borrowingService.borrowBook(1L)).thenReturn(testBorrowing);

        // when & then
        mockMvc.perform(post("/api/borrowings/borrow/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("BORROWED"));

        verify(borrowingService).borrowBook(1L);
    }

    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenBookNotAvailable() throws Exception {
        // given
        when(borrowingService.borrowBook(1L))
                .thenThrow(new IllegalArgumentException("Book is not available"));

        // when & then
        mockMvc.perform(post("/api/borrowings/borrow/1")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Book is not available"));
    }

    @Test
    @WithMockUser
    void shouldReturnBook() throws Exception {
        // given
        testBorrowing.setStatus(BorrowingStatus.RETURNED);
        testBorrowing.setReturnDate(LocalDate.now());
        when(borrowingService.returnBook(1L)).thenReturn(testBorrowing);

        // when & then
        mockMvc.perform(post("/api/borrowings/return/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book returned successfully"))
                .andExpect(jsonPath("$.borrowing.status").value("RETURNED"));

        verify(borrowingService).returnBook(1L);
    }

    @Test
    @WithMockUser
    void shouldGetUserBorrowingHistory() throws Exception {
        // given
        List<Borrowing> borrowings = Arrays.asList(testBorrowing);
        when(borrowingService.getUserBorrowingHistory()).thenReturn(borrowings);

        // when & then
        mockMvc.perform(get("/api/borrowings/my-history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser
    void shouldGetUserActiveBorrowings() throws Exception {
        // given
        List<Borrowing> activeBorrowings = Arrays.asList(testBorrowing);
        when(borrowingService.getUserActiveBorrowings()).thenReturn(activeBorrowings);

        // when & then
        mockMvc.perform(get("/api/borrowings/my-active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("BORROWED"));
    }

    @Test
    @WithMockUser
    void shouldGetBorrowingById() throws Exception {
        // given
        when(borrowingService.getBorrowingById(1L)).thenReturn(Optional.of(testBorrowing));

        // when & then
        mockMvc.perform(get("/api/borrowings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("BORROWED"));
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenBorrowingDoesNotExist() throws Exception {
        // given
        when(borrowingService.getBorrowingById(999L)).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(get("/api/borrowings/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllBorrowingsForAdmin() throws Exception {
        // given
        List<Borrowing> allBorrowings = Arrays.asList(testBorrowing);
        when(borrowingService.getAllBorrowings()).thenReturn(allBorrowings);

        // when & then
        mockMvc.perform(get("/api/borrowings/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldForbidGetAllBorrowingsForRegularUser() throws Exception {
        // when & then
        mockMvc.perform(get("/api/borrowings/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetOverdueBorrowings() throws Exception {
        // given
        testBorrowing.setStatus(BorrowingStatus.OVERDUE);
        List<Borrowing> overdueBorrowings = Arrays.asList(testBorrowing);
        when(borrowingService.getOverdueBorrowings()).thenReturn(overdueBorrowings);

        // when & then
        mockMvc.perform(get("/api/borrowings/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("OVERDUE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateOverdueBorrowings() throws Exception {
        // given
        doNothing().when(borrowingService).updateOverdueBorrowings();

        // when & then
        mockMvc.perform(post("/api/borrowings/update-overdue")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Overdue borrowings updated successfully"));

        verify(borrowingService).updateOverdueBorrowings();
    }
}
