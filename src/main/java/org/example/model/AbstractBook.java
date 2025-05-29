package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

// abstrakcyjna klasa bazowa dla roznych typow ksiazek
@Entity
@Table(name = "books")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "book_type", discriminatorType = DiscriminatorType.STRING)
public abstract class AbstractBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(length = 50)
    private String genre;

    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies;

    @Column(name = "total_copies", nullable = false)
    private Integer totalCopies;

    // relacja jeden do wielu z tabela Borrowing
    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Borrowing> borrowings = new HashSet<>();

    // konstruktory
    public AbstractBook() {}

    public AbstractBook(String title, String author, Integer availableCopies, Integer totalCopies) {
        this.title = title;
        this.author = author;
        this.availableCopies = availableCopies;
        this.totalCopies = totalCopies;
    }

    // metody abstrakcyjne dla podklas
    public abstract String getBookType();
    public abstract boolean isDigital();
    public abstract String getDisplayInfo();

    // gettery i settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public Integer getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(Integer availableCopies) { this.availableCopies = availableCopies; }

    public Integer getTotalCopies() { return totalCopies; }
    public void setTotalCopies(Integer totalCopies) { this.totalCopies = totalCopies; }

    public Set<Borrowing> getBorrowings() { return borrowings; }
    public void setBorrowings(Set<Borrowing> borrowings) { this.borrowings = borrowings; }

    // metody pomocnicze do zarzadzania wypozyczeniami
    public void addBorrowing(Borrowing borrowing) {
        borrowings.add(borrowing);
        borrowing.setBook(this);
    }

    public void removeBorrowing(Borrowing borrowing) {
        borrowings.remove(borrowing);
        borrowing.setBook(null);
    }
}
