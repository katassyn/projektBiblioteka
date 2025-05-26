package org.example.repository;

import org.example.model.AbstractBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// repozytorium dla operacji na tabeli ksiazek
@Repository
public interface BookRepository extends JpaRepository<AbstractBook, Long> {
    
    // znajduje ksiazki po tytule zawierajacym szukany tekst
    List<AbstractBook> findByTitleContainingIgnoreCase(String title);
    
    // znajduje ksiazki po autorze zawierajacym szukany tekst
    List<AbstractBook> findByAuthorContainingIgnoreCase(String author);
    
    // znajduje ksiazki po gatunku
    List<AbstractBook> findByGenreIgnoreCase(String genre);
    
    // znajduje dostepne ksiazki (availableCopies > 0)
    List<AbstractBook> findByAvailableCopiesGreaterThan(Integer copies);
    
    // wyszukuje ksiazki po tytule, autorze lub gatunku
    @Query("SELECT b FROM AbstractBook b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.genre) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<AbstractBook> searchBooks(@Param("searchTerm") String searchTerm);
}