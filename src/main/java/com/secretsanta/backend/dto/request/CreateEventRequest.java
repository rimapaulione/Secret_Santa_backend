package com.secretsanta.backend.dto.request;

import jakarta.validation.constraints.*;
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
public class CreateEventRequest {

    @NotBlank(message = "Event name is required")
    @Size(min = 3, max = 100, message = "Event name must be between 3 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Draw date is required")
    @Future(message = "Draw date must be in the future")
    private LocalDate drawDate;

    @Positive(message = "Budget must be a positive number")
    @Digits(integer = 10, fraction = 2, message = "Budget must have maximum 10 digits and 2 decimal places")
    private BigDecimal budget;
}
