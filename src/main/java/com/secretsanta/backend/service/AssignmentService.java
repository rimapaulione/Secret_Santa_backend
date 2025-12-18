package com.secretsanta.backend.service;

import com.secretsanta.backend.dto.response.AssignmentResponse;
import com.secretsanta.backend.exception.BadRequestException;
import com.secretsanta.backend.exception.ResourceNotFoundException;
import com.secretsanta.backend.model.Assignment;
import com.secretsanta.backend.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    /**
     * View assignment by access code
     * This is a public endpoint - no authentication required
     * Access code serves as the authentication
     */
    @Transactional(readOnly = true)
    public AssignmentResponse viewAssignment(String accessCodeStr) {
        // Parse access code
        UUID accessCode;
        try {
            accessCode = UUID.fromString(accessCodeStr);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid access code format");
        }

        // Find assignment by access code
        Assignment assignment = assignmentRepository.findByGiverAccessCode(accessCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No assignment found for this access code. " +
                        "Draw may not have been performed yet."
                ));

        // Return assignment response
        return AssignmentResponse.from(assignment);
    }
}
