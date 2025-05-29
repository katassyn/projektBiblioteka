package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dataTransfer.AuthenticationRequest;
import org.example.dataTransfer.BookRequest;
import org.example.dataTransfer.RegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// test integracyjny calej aplikacji bookstore
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BookstoreIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPerformCompleteUserFlow() throws Exception {
        // Test 1: Rejestracja nowego uzytkownika
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setUsername("integrationuser");
        registrationRequest.setPassword("password123");
        registrationRequest.setEmail("integration@test.com");
        registrationRequest.setFirstName("Integration");
        registrationRequest.setLastName("Test");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("integrationuser"));

        // Test 2: Logowanie uzytkownika
        AuthenticationRequest authRequest = new AuthenticationRequest();
        authRequest.setUsername("integrationuser");
        authRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("integrationuser"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldPerformAdminBookManagementFlow() throws Exception {
        // Test 1: Tworzenie ksiazki przez admina
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("Integration Book");
        bookRequest.setAuthor("Test Author");
        bookRequest.setGenre("Test Genre");
        bookRequest.setPublicationYear(2024);
        bookRequest.setTotalCopies(5);
        bookRequest.setBookType("PHYSICAL");

        mockMvc.perform(post("/api/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Book"));

        // Test 2: Pobranie wszystkich ksiazek
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Test 3: Wyszukiwanie ksiazek
        mockMvc.perform(get("/api/books/search")
                        .param("q", "Integration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    void shouldAccessPolymorphismEndpoints() throws Exception {
        // Test endpointow demonstrujacych polimorfizm
        mockMvc.perform(get("/api/poly/books/display"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/poly/books/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBooks").exists())
                .andExpect(jsonPath("$.digitalBooks").exists())
                .andExpect(jsonPath("$.physicalBooks").exists());

        mockMvc.perform(get("/api/poly/books/filter")
                        .param("digital", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldAccessUserProfileEndpoint() throws Exception {
        // Register a user first
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setUsername("profileuser");
        registrationRequest.setPassword("password123");
        registrationRequest.setEmail("profile@test.com");
        registrationRequest.setFirstName("Profile");
        registrationRequest.setLastName("Test");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk());

        // Login with the registered user
        AuthenticationRequest authRequest = new AuthenticationRequest();
        authRequest.setUsername("profileuser");
        authRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk());

        // Test dostępu do profilu użytkownika
        mockMvc.perform(get("/api/user/profile")
                        .with(user("profileuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAccessAdminEndpoints() throws Exception {
        // Test dostępu do endpointów administracyjnych
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/borrowings/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/borrowings/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldForbidUserAccessToAdminEndpoints() throws Exception {
        // Test blokowania dostępu zwykłych użytkowników do endpointów admin
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/borrowings/all"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAccessSwaggerDocumentation() throws Exception {
        // Test dostępu do dokumentacji Swagger
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }
}
