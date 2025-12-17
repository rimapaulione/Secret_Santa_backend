package com.secretsanta.backend.dto.response;

import com.secretsanta.backend.model.Participant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResponse {

    private Long id;
    private String name;
    private String email;
    private UUID accessCode;
    private Long eventId;
    private Boolean hasAssignment;
    private LocalDateTime createdAt;

    // Factory method to create from entity
    public static ParticipantResponse from(Participant participant) {
        return ParticipantResponse.builder()
                .id(participant.getId())
                .name(participant.getName())
                .email(participant.getEmail())
                .accessCode(participant.getAccessCode())
                .eventId(participant.getEvent().getId())
                .hasAssignment(participant.hasAssignment())
                .createdAt(participant.getCreatedAt())
                .build();
    }
}
