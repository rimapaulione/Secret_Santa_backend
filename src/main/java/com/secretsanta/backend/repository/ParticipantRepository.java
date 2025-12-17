package com.secretsanta.backend.repository;

import com.secretsanta.backend.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    /**
     * Find all participants for a specific event
     * Used in "Get Participants" endpoint
     */
    List<Participant> findByEventIdOrderByCreatedAtAsc(Long eventId);

    /**
     * Find participant by ID and verify they belong to correct event
     * Used for delete operations
     */
    Optional<Participant> findByIdAndEventId(Long id, Long eventId);

    /**
     * Check if email already exists in a specific event
     * Used for duplicate prevention
     */
    boolean existsByEventIdAndEmail(Long eventId, String email);

    /**
     * Find participant by access code
     * Used when participant views their assignment
     */
    Optional<Participant> findByAccessCode(UUID accessCode);

    /**
     * Find participant with their assignment eagerly loaded
     * Optimizes query when showing assignment
     */
    @Query("SELECT p FROM Participant p " +
           "LEFT JOIN FETCH p.givenAssignment a " +
           "LEFT JOIN FETCH a.receiver " +
           "WHERE p.accessCode = :accessCode")
    Optional<Participant> findByAccessCodeWithAssignment(@Param("accessCode") UUID accessCode);

    /**
     * Count participants in an event
     * Used for validation before draw
     */
    long countByEventId(Long eventId);
}
