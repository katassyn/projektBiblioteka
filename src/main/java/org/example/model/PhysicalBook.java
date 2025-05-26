package org.example.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// implementacja ksiazki fizycznej (polimorfizm)
@Entity
@DiscriminatorValue("PHYSICAL")
public class PhysicalBook extends AbstractBook {

    public PhysicalBook() {
        super();
    }

    public PhysicalBook(String title, String author, Integer availableCopies, Integer totalCopies) {
        super(title, author, availableCopies, totalCopies);
    }

    @Override
    public String getBookType() {
        return "Physical book";
    }

    @Override
    public boolean isDigital() {
        return false;
    }

    @Override
    public String getDisplayInfo() {
        return String.format("Book %s by %s (Physical copy - %d available)",
                getTitle(), getAuthor(), getAvailableCopies());
    }
}