package org.example.dataTransfer;

import io.swagger.v3.oas.annotations.media.Schema;

// klasa dla operacji na wypozyczeniach
@Schema(description = "Borrowing request for book operations")
public class BorrowingRequest {

    @Schema(description = "ID of the book to borrow", example = "1", required = true)
    private Long bookId;

    @Schema(description = "Optional notes for the borrowing", example = "Will return by next week")
    private String notes;

    // konstruktor domyslny
    public BorrowingRequest() {}

    // konstruktor z parametrami
    public BorrowingRequest(Long bookId, String notes) {
        this.bookId = bookId;
        this.notes = notes;
    }

    // gettery i settery
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
