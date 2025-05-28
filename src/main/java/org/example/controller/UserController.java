package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.model.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// kontroler dla userow
@RestController
@RequestMapping("/api")
@Tag(name = "Users", description = "User management operations")
@SecurityRequirement(name = "basicAuth")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // zwraca liste wszystkich userow tylko dla adminow
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Returns list of all users (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    // zwraca dane aktualnego usera
    @GetMapping("/user/profile")
    @Operation(summary = "Get current user profile", description = "Returns profile information of the authenticated user")
    @ApiResponse(responseCode = "200", description = "User profile retrieved successfully")
    public ResponseEntity<User> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }
}
