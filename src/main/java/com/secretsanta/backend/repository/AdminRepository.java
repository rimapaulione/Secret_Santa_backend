package com.secretsanta.backend.repository;

import com.secretsanta.backend.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * Find admin by email address
     * Used for login and checking if email exists
     */
    Optional<Admin> findByEmail(String email);

    /**
     * Check if email already exists
     * Used for registration validation
     */
    boolean existsByEmail(String email);
}
