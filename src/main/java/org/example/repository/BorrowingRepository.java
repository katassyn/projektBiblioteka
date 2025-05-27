package org.example.repository;

import org.example.model.Borrowing;
import org.example.model.BorrowingStatus;
import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// repozytorium dla operacji na tabeli wypozyczen
@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    
    // znajduje wypozyczenia po uzytkoniku
    List<Borrowing> findByUser(User user);
    
    // znajduje wypozyczenia po uzytkoniku i statusie
    List<Borrowing> findByUserAndStatus(User user, BorrowingStatus status);
    
    // znajduje wypozyczenia po statusie
    List<Borrowing> findByStatus(BorrowingStatus status);
    
    // znajduje aktywne wypozyczenie ksiazki przez uzytkownika
    @Query("SELECT b FROM Borrowing b WHERE b.user.id = :userId AND b.book.id = :bookId AND b.status IN ('RESERVED', 'BORROWED')")
    Optional<Borrowing> findActiveBorrowingByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
    
    // znajduje przeterminowane wypozyczenia
    @Query("SELECT b FROM Borrowing b WHERE b.status = 'BORROWED' AND b.dueDate < :currentDate")
    List<Borrowing> findOverdueBorrowings(@Param("currentDate") LocalDate currentDate);
    
    // sprawdza czy uzytkownik ma aktywne wypozyczenie ksiazki
    @Query("SELECT COUNT(b) > 0 FROM Borrowing b WHERE b.user.id = :userId AND b.book.id = :bookId AND b.status IN ('RESERVED', 'BORROWED')")
    boolean existsActiveBorrowingByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
}