package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.model.Borrowing;
import org.example.service.BorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// kontroler do zarzadzania wypozyczeniami
@RestController
@RequestMapping("/api/borrowings")
@Tag(name = "Borrowings", description = "Book borrowing and return operations")
@SecurityRequirement(name = "basicAuth")
public class BorrowingController {

    private final BorrowingService borrowingService;

    @Autowired
    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    // wypozycza ksiazke
    @PostMapping("/borrow/{bookId}")
    @Operation(summary = "Borrow a book", description = "Borrow a book for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book borrowed successfully"),
            @ApiResponse(responseCode = "400", description = "Book not available or already borrowed by user")
    })
    public ResponseEntity<?> borrowBook(
            @Parameter(description = "Book ID to borrow", required = true) @PathVariable Long bookId) {
        try {
            Borrowing borrowing = borrowingService.borrowBook(bookId);
            return ResponseEntity.ok(borrowing);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // zwraca ksiazke
    @PostMapping("/return/{borrowingId}")
    @Operation(summary = "Return a book", description = "Return a borrowed book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid borrowing ID or book already returned")
    })
    public ResponseEntity<?> returnBook(
            @Parameter(description = "Borrowing ID to return", required = true) @PathVariable Long borrowingId) {
        try {
            Borrowing borrowing = borrowingService.returnBook(borrowingId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Book returned successfully");
            response.put("borrowing", borrowing);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // zwraca historie wypozyczen uzytkownika
    @GetMapping("/my-history")
    @Operation(summary = "Get user borrowing history", description = "Returns all borrowings for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Borrowing history retrieved successfully")
    public List<Borrowing> getUserBorrowingHistory() {
        return borrowingService.getUserBorrowingHistory();
    }

    // zwraca aktywne wypozyczenia uzytkownika
    @GetMapping("/my-active")
    @Operation(summary = "Get user active borrowings", description = "Returns active borrowings for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Active borrowings retrieved successfully")
    public List<Borrowing> getUserActiveBorrowings() {
        return borrowingService.getUserActiveBorrowings();
    }

    // zwraca wypozyczenie po ID
    @GetMapping("/{id}")
    @Operation(summary = "Get borrowing by ID", description = "Returns a specific borrowing by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borrowing found"),
            @ApiResponse(responseCode = "404", description = "Borrowing not found")
    })
    public ResponseEntity<Borrowing> getBorrowingById(
            @Parameter(description = "Borrowing ID", required = true) @PathVariable Long id) {
        return borrowingService.getBorrowingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // zwraca wszystkie wypozyczenia (tylko admin)
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all borrowings", description = "Returns all borrowings in the system (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All borrowings retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public List<Borrowing> getAllBorrowings() {
        return borrowingService.getAllBorrowings();
    }

    // zwraca przeterminowane wypozyczenia (tylko admin)
    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get overdue borrowings", description = "Returns all overdue borrowings (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Overdue borrowings retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public List<Borrowing> getOverdueBorrowings() {
        return borrowingService.getOverdueBorrowings();
    }

    // aktualizuje przeterminowane wypozyczenia (tylko admin)
    @PostMapping("/update-overdue")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update overdue borrowings", description = "Updates status of overdue borrowings (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Overdue borrowings updated successfully"),
            @ApiResponse(responseCode = "400", description = "Failed to update overdue borrowings"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<?> updateOverdueBorrowings() {
        try {
            borrowingService.updateOverdueBorrowings();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Overdue borrowings updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update overdue borrowings");
            return ResponseEntity.badRequest().body(error);
        }
    }
}
