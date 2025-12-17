package com.secretsanta.backend.dto.response;

import com.secretsanta.backend.model.Admin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponse {

    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;

    // Factory method to create from entity
    public static AdminResponse from(Admin admin) {
        return AdminResponse.builder()
                .id(admin.getId())
                .name(admin.getName())
                .email(admin.getEmail())
                .createdAt(admin.getCreatedAt())
                .build();
    }
}
