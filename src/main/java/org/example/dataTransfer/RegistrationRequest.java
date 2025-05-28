package org.example.dataTransfer;

import io.swagger.v3.oas.annotations.media.Schema;

// klasa dla rejestracji w aplikacji
@Schema(description = "User registration request")
public class RegistrationRequest {

    @Schema(description = "Username", example = "john_doe", required = true)
    private String username;

    @Schema(description = "Password", example = "password123", required = true)
    private String password;

    @Schema(description = "Email address", example = "john@example.com", required = true)
    private String email;

    @Schema(description = "First name", example = "John")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    public RegistrationRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
