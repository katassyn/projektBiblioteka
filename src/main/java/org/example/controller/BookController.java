package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Books", description = "Book management operations")
@SecurityRequirement(name = "basicAuth")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // zwraca wszystkie ksiazki
    @GetMapping
    @Operation(summary = "Get all books", description = "Returns list of all books in the system")
    @ApiResponse(responseCode = "200", description = "Books retrieved successfully")
    public List<AbstractBook> getAllBooks() {
        return bookService.getAllBooks();
    }

    // zwraca ksiazke po ID
    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Returns a specific book by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<AbstractBook> getBookById(
            @Parameter(description = "Book ID", required = true) @PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // tworzy nowa ksiazke (tylko admin)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new book", description = "Creates a new book (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid book data"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
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
    @Operation(summary = "Update book", description = "Updates an existing book (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "400", description = "Invalid book data"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<?> updateBook(
            @Parameter(description = "Book ID", required = true) @PathVariable Long id,
            @RequestBody BookRequest request) {
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
    @Operation(summary = "Delete book", description = "Deletes a book (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<?> deleteBook(
            @Parameter(description = "Book ID", required = true) @PathVariable Long id) {
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

    // wyszukuje ksiazki
    @GetMapping("/search")
    @Operation(summary = "Search books", description = "Search books by title, author, or genre")
    @ApiResponse(responseCode = "200", description = "Search results returned")
    public List<AbstractBook> searchBooks(
            @Parameter(description = "Search term") @RequestParam(required = false) String q) {
        return bookService.searchBooks(q);
    }

    // zwraca ksiazki po gatunku
    @GetMapping("/genre/{genre}")
    @Operation(summary = "Get books by genre", description = "Returns books of a specific genre")
    @ApiResponse(responseCode = "200", description = "Books retrieved successfully")
    public List<AbstractBook> getBooksByGenre(
            @Parameter(description = "Genre name", required = true) @PathVariable String genre) {
        return bookService.getBooksByGenre(genre);
    }

    // zwraca tylko dostepne ksiazki
    @GetMapping("/available")
    @Operation(summary = "Get available books", description = "Returns books that are available for borrowing")
    @ApiResponse(responseCode = "200", description = "Available books retrieved successfully")
    public List<AbstractBook> getAvailableBooks() {
        return bookService.getAvailableBooks();
    }
}
