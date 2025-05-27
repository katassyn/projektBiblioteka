package org.example.dataTransfer;

// klasa dla operacji na wypozyczeniach
public class BorrowingRequest {
    private Long bookId;
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