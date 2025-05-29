package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private User user;
    private Borrowing borrowing;
    private AbstractBook book;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "password", "test@example.com", Role.USER);
        user.setId(1L);
        user.setFirstName("Test");
        user.setLastName("User");

        book = new PhysicalBook("Test Book", "Test Author", 3, 3);
        book.setId(1L);

        borrowing = new Borrowing(
                null, // user will be set by addBorrowing
                book,
                BorrowingStatus.BORROWED,
                java.time.LocalDate.now(),
                java.time.LocalDate.now().plusDays(14)
        );
        borrowing.setId(1L);
    }

    @Test
    void shouldAddBorrowing() {
        // when
        user.addBorrowing(borrowing);

        // then
        assertThat(user.getBorrowings()).contains(borrowing);
        assertThat(borrowing.getUser()).isEqualTo(user);
    }

    @Test
    void shouldRemoveBorrowing() {
        // given
        user.addBorrowing(borrowing);
        assertThat(user.getBorrowings()).contains(borrowing);

        // when
        user.removeBorrowing(borrowing);

        // then
        assertThat(user.getBorrowings()).doesNotContain(borrowing);
        assertThat(borrowing.getUser()).isNull();
    }

    @Test
    void shouldCreateUserWithConstructor() {
        // when
        User newUser = new User("newuser", "newpassword", "new@example.com", Role.ADMIN);

        // then
        assertThat(newUser.getUsername()).isEqualTo("newuser");
        assertThat(newUser.getPassword()).isEqualTo("newpassword");
        assertThat(newUser.getEmail()).isEqualTo("new@example.com");
        assertThat(newUser.getRole()).isEqualTo(Role.ADMIN);
        assertThat(newUser.getBorrowings()).isEmpty();
    }

    @Test
    void shouldSetAndGetProperties() {
        // given
        User newUser = new User();

        // when
        newUser.setId(2L);
        newUser.setUsername("newuser");
        newUser.setPassword("newpassword");
        newUser.setEmail("new@example.com");
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setRole(Role.ADMIN);

        // then
        assertThat(newUser.getId()).isEqualTo(2L);
        assertThat(newUser.getUsername()).isEqualTo("newuser");
        assertThat(newUser.getPassword()).isEqualTo("newpassword");
        assertThat(newUser.getEmail()).isEqualTo("new@example.com");
        assertThat(newUser.getFirstName()).isEqualTo("New");
        assertThat(newUser.getLastName()).isEqualTo("User");
        assertThat(newUser.getRole()).isEqualTo(Role.ADMIN);
    }
}