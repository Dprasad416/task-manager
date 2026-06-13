package com.taskmanager.repository;

import com.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// JpaRepository gives you save(), findById(), findAll(), delete() for FREE
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring auto-generates SQL: SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);

    // Spring auto-generates SQL: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // Check if username already exists
    boolean existsByUsername(String username);

    // Check if email already exists
    boolean existsByEmail(String email);
}
