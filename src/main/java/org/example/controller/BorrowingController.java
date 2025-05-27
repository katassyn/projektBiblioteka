package org.example.controller;

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
public class BorrowingController {

    private final BorrowingService borrowingService;

    @Autowired
    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    // wypozycza ksiazke (dostepne dla uwierzytelnionych uzytkownikow)
    @PostMapping("/borrow/{bookId}")
    public ResponseEntity<?> borrowBook(@PathVariable Long bookId) {
        try {
            Borrowing borrowing = borrowingService.borrowBook(bookId);
            return ResponseEntity.ok(borrowing);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // zwraca ksiazke (dostepne dla uwierzytelnionych uzytkownikow)
    @PostMapping("/return/{borrowingId}")
    public ResponseEntity<?> returnBook(@PathVariable Long borrowingId) {
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

    // zwraca historie wypozyczen uzytkownika (dostepne dla uwierzytelnionych uzytkownikow)
    @GetMapping("/my-history")
    public List<Borrowing> getUserBorrowingHistory() {
        return borrowingService.getUserBorrowingHistory();
    }

    // zwraca aktywne wypozyczenia uzytkownika (dostepne dla uwierzytelnionych uzytkownikow)
    @GetMapping("/my-active")
    public List<Borrowing> getUserActiveBorrowings() {
        return borrowingService.getUserActiveBorrowings();
    }

    // zwraca wypozyczenie po ID (dostepne dla uwierzytelnionych uzytkownikow)
    @GetMapping("/{id}")
    public ResponseEntity<Borrowing> getBorrowingById(@PathVariable Long id) {
        return borrowingService.getBorrowingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // zwraca wszystkie wypozyczenia (tylko admin)
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Borrowing> getAllBorrowings() {
        return borrowingService.getAllBorrowings();
    }

    // zwraca przeterminowane wypozyczenia (tylko admin)
    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Borrowing> getOverdueBorrowings() {
        return borrowingService.getOverdueBorrowings();
    }

    // aktualizuje przeterminowane wypozyczenia (tylko admin)
    @PostMapping("/update-overdue")
    @PreAuthorize("hasRole('ADMIN')")
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