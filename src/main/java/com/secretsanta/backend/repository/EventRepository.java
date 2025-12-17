package com.secretsanta.backend.repository;

import com.secretsanta.backend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Find all events belonging to a specific admin
     * Used in "Get All Events" endpoint
     */
    List<Event> findByAdminIdOrderByCreatedAtDesc(Long adminId);

    /**
     * Find event by ID and admin ID
     * Used to verify admin owns the event before allowing operations
     */
    Optional<Event> findByIdAndAdminId(Long id, Long adminId);

    /**
     * Check if event exists and belongs to admin
     * Quick existence check without fetching full entity
     */
    boolean existsByIdAndAdminId(Long id, Long adminId);

    /**
     * Find event with participants eagerly loaded
     * Prevents N+1 query problem when accessing participants
     */
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.participants WHERE e.id = :id")
    Optional<Event> findByIdWithParticipants(@Param("id") Long id);

    /**
     * Find event with assignments eagerly loaded
     * Used when checking draw status
     */
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.assignments WHERE e.id = :id")
    Optional<Event> findByIdWithAssignments(@Param("id") Long id);
}
