package org.example.controller;

import org.example.dataTransfer.BookRequest;
import org.example.model.AbstractBook;
import org.example.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// kontroler do zarzadzania ksiazkami
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // zwraca wszystkie ksiazki (dostepne dla wszystkich uwierzytelnionych uzytkownikow)
    @GetMapping
    public List<AbstractBook> getAllBooks() {
        return bookService.getAllBooks();
    }

    // zwraca ksiazke po ID (dostepne dla wszystkich uwierzytelnionych uzytkownikow)
    @GetMapping("/{id}")
    public ResponseEntity<AbstractBook> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // tworzy nowa ksiazke (tylko admin)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createBook(@RequestBody BookRequest request) {
        try {
            AbstractBook book = bookService.createBook(
                    request.getTitle(),
                    request.getAuthor(),
                    request.getPublicationYear(),
                    request.getGenre(),
                    request.getTotalCopies(),
                    request.getBookType() != null ? request.getBookType() : "PHYSICAL"
            );
            return ResponseEntity.ok(book);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // aktualizuje istniejaca ksiazke (tylko admin)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody BookRequest request) {
        try {
            AbstractBook book = bookService.updateBook(
                    id,
                    request.getTitle(),
                    request.getAuthor(),
                    request.getPublicationYear(),
                    request.getGenre(),
                    request.getTotalCopies()
            );
            return ResponseEntity.ok(book);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // usuwa ksiazke (tylko admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Book deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // wyszukuje ksiazki (dostepne dla wszystkich uwierzytelnionych uzytkownikow)
    @GetMapping("/search")
    public List<AbstractBook> searchBooks(@RequestParam(required = false) String q) {
        return bookService.searchBooks(q);
    }

    // zwraca ksiazki po gatunku (dostepne dla wszystkich uwierzytelnionych uzytkownikow)
    @GetMapping("/genre/{genre}")
    public List<AbstractBook> getBooksByGenre(@PathVariable String genre) {
        return bookService.getBooksByGenre(genre);
    }

    // zwraca tylko dostepne ksiazki (dostepne dla wszystkich uwierzytelnionych uzytkownikow)
    @GetMapping("/available")
    public List<AbstractBook> getAvailableBooks() {
        return bookService.getAvailableBooks();
    }
}