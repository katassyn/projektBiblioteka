package org.example.repository;

import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// interfejs do operacji na encji user
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //szuka uzytkownika po nazwie
    Optional<User> findByUsername(String username);

    // szuka uzytkownika po email
    Optional<User> findByEmail(String email);

    // sprawdza czy uzytkownik o podanym loginie istnieje
    boolean existsByUsername(String username);

    // sprawdza czy uzytkownik o podanym email istnieje
    boolean existsByEmail(String email);
}