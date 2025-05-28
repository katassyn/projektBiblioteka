package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dataTransfer.AuthenticationRequest;
import org.example.dataTransfer.RegistrationRequest;
import org.example.model.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

// kontroler do uwierzytelniania
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration and login operations")
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    // rejestrujemy nowego uzytkownika, zwracamy dane nowego usera
    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Creates a new user account with USER role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Username or email already exists",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        try {
            User user = userService.registerUser(
                    request.getUsername(),
                    request.getPassword(),
                    request.getEmail(),
                    request.getFirstName(),
                    request.getLastName()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // uwierzytelniamy uzytkownika, zwracamy dane uzytkownika
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user and returns user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Authentication failed",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userService.findByUsername(request.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("role", user.getRole());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Authentication failed");
            return ResponseEntity.badRequest().body(error);
        }
    }
}
