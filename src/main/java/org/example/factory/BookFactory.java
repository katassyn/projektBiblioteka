package org.example.factory;

import org.example.model.*;
import org.springframework.stereotype.Component;

// implementacja Factory do tworzenia roznych typow ksiazek
@Component
public class BookFactory {

    //tworzy ksiazke
    public AbstractBook createBook(BookType bookType, String title, String author, 
                                  Integer availableCopies, Integer totalCopies) {
        switch (bookType) {
            case PHYSICAL:
                return new PhysicalBook(title, author, availableCopies, totalCopies);
            case EBOOK:
                return new EBook(title, author, availableCopies, totalCopies);
            case AUDIOBOOK:
                return new AudioBook(title, author, availableCopies, totalCopies);
            default:
                throw new IllegalArgumentException("Nieznany typ ksiazki: " + bookType);
        }
    }

    // tworzy ksiazke z typu tekstowego
    public AbstractBook createBook(String bookTypeStr, String title, String author, 
                                  Integer availableCopies, Integer totalCopies) {
        try {
            BookType bookType = BookType.valueOf(bookTypeStr.toUpperCase());
            return createBook(bookType, title, author, availableCopies, totalCopies);
        } catch (IllegalArgumentException e) {
            // domyslnie tworz ksiazke fizyczna jesli typ jest nieznany
            return new PhysicalBook(title, author, availableCopies, totalCopies);
        }
    }
}