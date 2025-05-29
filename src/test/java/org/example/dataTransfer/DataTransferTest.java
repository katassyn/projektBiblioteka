package org.example.dataTransfer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataTransferTest {

    @Test
    void shouldTestAuthenticationRequest() {
        // given
        AuthenticationRequest request = new AuthenticationRequest();

        // when
        request.setUsername("testuser");
        request.setPassword("password123");

        // then
        assertThat(request.getUsername()).isEqualTo("testuser");
        assertThat(request.getPassword()).isEqualTo("password123");
    }

    @Test
    void shouldTestBookRequest() {
        // given
        BookRequest request = new BookRequest();

        // when
        request.setTitle("Test Book");
        request.setAuthor("Test Author");
        request.setPublicationYear(2023);
        request.setGenre("Fiction");
        request.setTotalCopies(3);
        request.setBookType("PHYSICAL");

        // then
        assertThat(request.getTitle()).isEqualTo("Test Book");
        assertThat(request.getAuthor()).isEqualTo("Test Author");
        assertThat(request.getPublicationYear()).isEqualTo(2023);
        assertThat(request.getGenre()).isEqualTo("Fiction");
        assertThat(request.getTotalCopies()).isEqualTo(3);
        assertThat(request.getBookType()).isEqualTo("PHYSICAL");
    }

    @Test
    void shouldTestBorrowingRequest() {
        // given
        BorrowingRequest request = new BorrowingRequest();

        // when
        request.setBookId(1L);
        request.setNotes("Test notes");

        // then
        assertThat(request.getBookId()).isEqualTo(1L);
        assertThat(request.getNotes()).isEqualTo("Test notes");
    }

    @Test
    void shouldTestBorrowingRequestWithConstructor() {
        // when
        BorrowingRequest request = new BorrowingRequest(1L, "Test notes");

        // then
        assertThat(request.getBookId()).isEqualTo(1L);
        assertThat(request.getNotes()).isEqualTo("Test notes");
    }

    @Test
    void shouldTestRegistrationRequest() {
        // given
        RegistrationRequest request = new RegistrationRequest();

        // when
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setEmail("test@example.com");
        request.setFirstName("Test");
        request.setLastName("User");

        // then
        assertThat(request.getUsername()).isEqualTo("testuser");
        assertThat(request.getPassword()).isEqualTo("password123");
        assertThat(request.getEmail()).isEqualTo("test@example.com");
        assertThat(request.getFirstName()).isEqualTo("Test");
        assertThat(request.getLastName()).isEqualTo("User");
    }
}