package org.example.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// implementacja ksiazki elektronicznej
@Entity
@DiscriminatorValue("EBOOK")
public class EBook extends AbstractBook {

    public EBook() {
        super();
    }

    public EBook(String title, String author, Integer availableCopies, Integer totalCopies) {
        super(title, author, availableCopies, totalCopies);
    }

    @Override
    public String getBookType() {
        return "eBook";
    }

    @Override
    public boolean isDigital() {
        return true;
    }

    @Override
    public String getDisplayInfo() {
        return String.format("Book %s by %s  (Digital copy - %d licenses available)",
                getTitle(), getAuthor(), getAvailableCopies());
    }
}