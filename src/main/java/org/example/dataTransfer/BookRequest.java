package org.example.dataTransfer;

import io.swagger.v3.oas.annotations.media.Schema;

// klasa dla operacji na ksiazkach (tworzenie/aktualizacja)
@Schema(description = "Book creation/update request")
public class BookRequest {

    @Schema(description = "Book title", example = "The Great Gatsby", required = true)
    private String title;

    @Schema(description = "Author name", example = "F. Scott Fitzgerald", required = true)
    private String author;

    @Schema(description = "Publication year", example = "1925")
    private Integer publicationYear;

    @Schema(description = "Book genre", example = "Fiction")
    private String genre;

    @Schema(description = "Total number of copies", example = "5", required = true)
    private Integer totalCopies;

    @Schema(description = "Book type", example = "PHYSICAL", 
            allowableValues = {"PHYSICAL", "EBOOK", "AUDIOBOOK"})
    private String bookType;

    // konstruktor domyslny
    public BookRequest() {}

    // gettery i settery
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(Integer totalCopies) {
        this.totalCopies = totalCopies;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }
}
