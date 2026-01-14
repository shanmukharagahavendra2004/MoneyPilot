package com.example.financetracker.repo;

import com.example.financetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

    // Defensive Optional returns to avoid null
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    // Retrieve only ID by username
    @Query("SELECT u.id FROM User u WHERE u.username = :username")
    Long findIdByUsername(@Param("username") String username);

    // Find user by username or email
    @Query("SELECT u FROM User u WHERE u.username = :input OR u.email = :input")
    Optional<User> findByUsernameOrEmail(@Param("input") String input);

    // Optional defensive query helpers (example usage)
    default Optional<User> safeFindByUsernameOrEmail(String input) {
        if (input == null || input.trim().isEmpty()) {
            return Optional.empty();
        }
        return findByUsernameOrEmail(input.trim());
    }
}
