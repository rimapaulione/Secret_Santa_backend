package com.secretsanta.backend.dto.response;

import com.secretsanta.backend.model.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDate drawDate;
    private BigDecimal budget;
    private Boolean isLocked;
    private Long adminId;
    private Integer participantCount;
    private LocalDateTime createdAt;

    // Factory method to create from entity
    public static EventResponse from(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .drawDate(event.getDrawDate())
                .budget(event.getBudget())
                .isLocked(event.getIsLocked())
                .adminId(event.getAdmin().getId())
                .participantCount(event.getParticipantCount())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
