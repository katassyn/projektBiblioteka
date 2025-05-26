package org.example.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// implementacja audiobooka
@Entity
@DiscriminatorValue("AUDIOBOOK")
public class AudioBook extends AbstractBook {

    public AudioBook() {
        super();
    }

    public AudioBook(String title, String author, Integer availableCopies, Integer totalCopies) {
        super(title, author, availableCopies, totalCopies);
    }

    @Override
    public String getBookType() {
        return "Audiobook";
    }

    @Override
    public boolean isDigital() {
        return true;
    }

    @Override
    public String getDisplayInfo() {
        return String.format("Book %s by %s (Audio format - %d licenses available)",
                getTitle(), getAuthor(), getAvailableCopies());
    }
}