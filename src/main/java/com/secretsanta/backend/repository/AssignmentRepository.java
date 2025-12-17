package com.secretsanta.backend.repository;

import com.secretsanta.backend.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    /**
     * Find all assignments for a specific event
     * Used to check if draw has been performed
     */
    List<Assignment> findByEventId(Long eventId);

    /**
     * Check if assignments exist for an event
     * Quick existence check
     */
    boolean existsByEventId(Long eventId);

    /**
     * Find assignment for a specific giver
     * Returns who this participant is giving to
     */
    Optional<Assignment> findByGiverId(Long giverId);

    /**
     * Find assignment by giver's access code
     * Used when participant views their assignment
     */
    @Query("SELECT a FROM Assignment a " +
           "JOIN a.giver g " +
           "JOIN FETCH a.receiver " +
           "WHERE g.accessCode = :accessCode")
    Optional<Assignment> findByGiverAccessCode(@Param("accessCode") java.util.UUID accessCode);

    /**
     * Delete all assignments for an event
     * Used when redrawing
     */
    @Modifying
    @Query("DELETE FROM Assignment a WHERE a.event.id = :eventId")
    void deleteByEventId(@Param("eventId") Long eventId);

    /**
     * Count assignments for an event
     * Used for verification
     */
    long countByEventId(Long eventId);
}
