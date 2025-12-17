package com.secretsanta.backend.dto.response;

import com.secretsanta.backend.model.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDetailResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDate drawDate;
    private BigDecimal budget;
    private Boolean isLocked;
    private Long adminId;
    private List<ParticipantResponse> participants;
    private LocalDateTime createdAt;

    // Factory method to create from entity
    public static EventDetailResponse from(Event event) {
        return EventDetailResponse.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .drawDate(event.getDrawDate())
                .budget(event.getBudget())
                .isLocked(event.getIsLocked())
                .adminId(event.getAdmin().getId())
                .participants(event.getParticipants().stream()
                        .map(ParticipantResponse::from)
                        .collect(Collectors.toList()))
                .createdAt(event.getCreatedAt())
                .build();
    }
}
