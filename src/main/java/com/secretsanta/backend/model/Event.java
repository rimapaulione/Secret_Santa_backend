package com.secretsanta.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "draw_date", nullable = false)
    private LocalDate drawDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal budget;

    @Column(name = "is_locked", nullable = false)
    @Builder.Default
    private Boolean isLocked = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Participant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Assignment> assignments = new ArrayList<>();

    // Helper methods for managing relationships
    public void addParticipant(Participant participant) {
        participants.add(participant);
        participant.setEvent(this);
    }

    public void removeParticipant(Participant participant) {
        participants.remove(participant);
        participant.setEvent(null);
    }

    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
        assignment.setEvent(this);
    }

    public void clearAssignments() {
        assignments.clear();
    }

    // Business logic methods
    public boolean hasDrawBeenPerformed() {
        return !assignments.isEmpty();
    }

    public int getParticipantCount() {
        return participants.size();
    }

    public boolean canPerformDraw() {
        return !isLocked && participants.size() >= 3;
    }

    public boolean canBeModified() {
        return !isLocked;
    }
}
