package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.model.AbstractBook;
import org.example.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// kontroler demonstrujacy polimorfizm
@RestController
@RequestMapping("/api/poly")
@Tag(name = "Polymorphism Demo", description = "Endpoints demonstrating polymorphic behavior of books")
@SecurityRequirement(name = "basicAuth")
public class PolyBookController {

    private final BookService bookService;

    @Autowired
    public PolyBookController(BookService bookService) {
        this.bookService = bookService;
    }

    // zwraca wszystkie ksiazki z polimorficznymi informacjami wyswietlania
    @GetMapping("/books/display")
    @Operation(summary = "Get books with polymorphic display info", 
               description = "Returns all books with their polymorphic display information")
    @ApiResponse(responseCode = "200", description = "Books with display info retrieved successfully")
    public ResponseEntity<List<Map<String, Object>>> getBooksWithDisplayInfo() {
        List<AbstractBook> books = bookService.getAllBooks();

        List<Map<String, Object>> bookDisplayList = books.stream().map(book -> {
            Map<String, Object> bookInfo = new HashMap<>();
            bookInfo.put("id", book.getId());
            bookInfo.put("title", book.getTitle());
            bookInfo.put("author", book.getAuthor());
            bookInfo.put("type", book.getBookType()); // polimorfizm
            bookInfo.put("isDigital", book.isDigital()); // polimorfizm
            bookInfo.put("displayInfo", book.getDisplayInfo()); // polimorfizm
            return bookInfo;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(bookDisplayList);
    }

    // zwraca statystyki ksiazek cyfrowych vs fizycznych
    @GetMapping("/books/stats")
    @Operation(summary = "Get book statistics", 
               description = "Returns statistics about digital vs physical books")
    @ApiResponse(responseCode = "200", description = "Book statistics retrieved successfully")
    public ResponseEntity<Map<String, Object>> getBookStats() {
        List<AbstractBook> books = bookService.getAllBooks();

        long digitalBooks = books.stream().filter(AbstractBook::isDigital).count();
        long physicalBooks = books.stream().filter(book -> !book.isDigital()).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBooks", books.size());
        stats.put("digitalBooks", digitalBooks);
        stats.put("physicalBooks", physicalBooks);

        return ResponseEntity.ok(stats);
    }

    // zwraca ksiazki wedlug filtru cyfrowe/fizyczne
    @GetMapping("/books/filter")
    @Operation(summary = "Filter books by type", 
               description = "Returns books filtered by digital or physical type")
    @ApiResponse(responseCode = "200", description = "Filtered books retrieved successfully")
    public ResponseEntity<List<AbstractBook>> getBooksByDigitalFilter(
            @Parameter(description = "Filter by digital (true) or physical (false)", required = true)
            @RequestParam boolean digital) {
        List<AbstractBook> books = bookService.getAllBooks();

        List<AbstractBook> filteredBooks = books.stream()
                .filter(book -> book.isDigital() == digital)
                .collect(Collectors.toList());

        return ResponseEntity.ok(filteredBooks);
    }
}
