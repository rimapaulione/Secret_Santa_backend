package com.secretsanta.backend.controller;

import com.secretsanta.backend.dto.response.AssignmentResponse;
import com.secretsanta.backend.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    /**
     * GET /api/assignments/{accessCode}
     * View assignment by access code (public endpoint)
     */
    @GetMapping("/{accessCode}")
    public ResponseEntity<AssignmentResponse> viewAssignment(
            @PathVariable String accessCode
    ) {
        AssignmentResponse response = assignmentService.viewAssignment(accessCode);
        return ResponseEntity.ok(response);
    }
}
