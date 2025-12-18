package com.secretsanta.backend.controller;

import com.secretsanta.backend.dto.request.AddParticipantRequest;
import com.secretsanta.backend.dto.response.ParticipantResponse;
import com.secretsanta.backend.service.ParticipantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ParticipantController {

    @Autowired
    private ParticipantService participantService;

    /**
     * POST /api/events/{eventId}/participants
     * Add a participant to an event
     */
    @PostMapping("/events/{eventId}/participants")
    public ResponseEntity<ParticipantResponse> addParticipant(
            @PathVariable Long eventId,
            @Valid @RequestBody AddParticipantRequest request
    ) {
        ParticipantResponse response = participantService.addParticipant(eventId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET /api/events/{eventId}/participants
     * Get all participants for an event
     */
    @GetMapping("/events/{eventId}/participants")
    public ResponseEntity<List<ParticipantResponse>> getParticipants(
            @PathVariable Long eventId
    ) {
        List<ParticipantResponse> participants = participantService.getParticipants(eventId);
        return ResponseEntity.ok(participants);
    }

    /**
     * DELETE /api/participants/{id}
     * Remove a participant from an event
     */
    @DeleteMapping("/participants/{id}")
    public ResponseEntity<Void> removeParticipant(@PathVariable Long id) {
        participantService.removeParticipant(id);
        return ResponseEntity.noContent().build();
    }
}
