package com.secretsanta.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "participants",
    uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "email"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "access_code", nullable = false, unique = true)
    private UUID accessCode;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @OneToOne(mappedBy = "giver", cascade = CascadeType.ALL)
    private Assignment givenAssignment;

    // Generate access code before persisting
    @PrePersist
    private void generateAccessCode() {
        if (accessCode == null) {
            accessCode = UUID.randomUUID();
        }
    }

    // Business logic methods
    public boolean hasAssignment() {
        return givenAssignment != null;
    }
}
