package com.secretsanta.backend.dto.response;

import com.secretsanta.backend.model.Assignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentResponse {

    private GiverInfo giver;
    private RecipientInfo recipient;
    private EventInfo event;
    private String message;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GiverInfo {
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipientInfo {
        private String name;
        private String email;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventInfo {
        private String name;
        private LocalDate drawDate;
        private BigDecimal budget;
        private String description;
    }

    // Factory method to create from assignment
    public static AssignmentResponse from(Assignment assignment) {
        return AssignmentResponse.builder()
                .giver(GiverInfo.builder()
                        .name(assignment.getGiver().getName())
                        .build())
                .recipient(RecipientInfo.builder()
                        .name(assignment.getReceiver().getName())
                        .email(assignment.getReceiver().getEmail())
                        .build())
                .event(EventInfo.builder()
                        .name(assignment.getEvent().getName())
                        .drawDate(assignment.getEvent().getDrawDate())
                        .budget(assignment.getEvent().getBudget())
                        .description(assignment.getEvent().getDescription())
                        .build())
                .message("You are Secret Santa for " + assignment.getReceiver().getName() + "!")
                .build();
    }
}
