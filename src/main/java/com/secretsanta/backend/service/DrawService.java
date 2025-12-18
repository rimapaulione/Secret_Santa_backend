package com.secretsanta.backend.service;

import com.secretsanta.backend.dto.request.DrawRequest;
import com.secretsanta.backend.dto.response.DrawResponse;
import com.secretsanta.backend.exception.BadRequestException;
import com.secretsanta.backend.model.Assignment;
import com.secretsanta.backend.model.Event;
import com.secretsanta.backend.model.Participant;
import com.secretsanta.backend.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DrawService {

    @Autowired
    private EventService eventService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private AssignmentRepository assignmentRepository;

    // Maximum retry attempts for draw algorithm
    private static final int MAX_DRAW_ATTEMPTS = 100;

    /**
     * Perform the Secret Santa draw
     * Assigns each participant to give a gift to another participant
     */
    @Transactional
    public DrawResponse performDraw(Long eventId, DrawRequest request) {
        Event event = eventService.findEventByIdAndVerifyOwnership(eventId);

        // Validation: Check if event is locked
        if (event.getIsLocked()) {
            throw new BadRequestException("Cannot perform draw on locked event");
        }

        // Validation: Check if draw already exists
        boolean drawExists = assignmentRepository.existsByEventId(eventId);
        if (drawExists && !request.getAllowRedraw()) {
            throw new BadRequestException(
                    "Draw already exists for this event. Set allowRedraw to true to redraw."
            );
        }

        // Get all participants
        List<Participant> participants = participantService.getParticipantEntities(eventId);

        // Validation: Minimum 3 participants required
        if (participants.size() < 3) {
            throw new BadRequestException(
                    "Minimum 3 participants required. Current count: " + participants.size()
            );
        }

        // If redraw is allowed, delete existing assignments
        if (drawExists && request.getAllowRedraw()) {
            assignmentRepository.deleteByEventId(eventId);
        }

        // Perform the draw with retry logic
        List<Assignment> assignments = performDrawWithRetry(event, participants);

        // Save all assignments
        assignmentRepository.saveAll(assignments);

        // Return response
        return DrawResponse.builder()
                .success(true)
                .message("Gift draw completed successfully")
                .eventId(eventId)
                .assignmentCount(assignments.size())
                .drawDate(LocalDateTime.now())
                .build();
    }

    /**
     * Perform draw with retry logic
     * Retries if invalid assignment is generated (e.g., self-assignment)
     */
    private List<Assignment> performDrawWithRetry(Event event, List<Participant> participants) {
        for (int attempt = 1; attempt <= MAX_DRAW_ATTEMPTS; attempt++) {
            try {
                return generateAssignments(event, participants);
            } catch (IllegalStateException e) {
                // Retry on failure
                if (attempt == MAX_DRAW_ATTEMPTS) {
                    throw new BadRequestException(
                            "Failed to generate valid draw after " + MAX_DRAW_ATTEMPTS +
                            " attempts. Please try again."
                    );
                }
            }
        }
        throw new BadRequestException("Draw failed unexpectedly");
    }

    /**
     * Generate assignments using derangement algorithm
     * Ensures no participant gives to themselves
     */
    private List<Assignment> generateAssignments(Event event, List<Participant> participants) {
        List<Assignment> assignments = new ArrayList<>();

        // Create two lists: givers and receivers
        List<Participant> givers = new ArrayList<>(participants);
        List<Participant> receivers = new ArrayList<>(participants);

        // Shuffle receivers
        Collections.shuffle(receivers);

        // Verify no one is assigned to themselves (derangement check)
        for (int i = 0; i < givers.size(); i++) {
            if (givers.get(i).getId().equals(receivers.get(i).getId())) {
                // Invalid assignment found, throw exception to retry
                throw new IllegalStateException("Self-assignment detected");
            }
        }

        // Create assignments
        for (int i = 0; i < givers.size(); i++) {
            Assignment assignment = Assignment.builder()
                    .event(event)
                    .giver(givers.get(i))
                    .receiver(receivers.get(i))
                    .build();
            assignments.add(assignment);
        }

        return assignments;
    }

    /**
     * Alternative draw algorithm: Circular shift
     * More predictable but less random
     */
    @SuppressWarnings("unused")
    private List<Assignment> generateAssignmentsCircular(Event event, List<Participant> participants) {
        List<Assignment> assignments = new ArrayList<>();

        // Shuffle participants first
        List<Participant> shuffled = new ArrayList<>(participants);
        Collections.shuffle(shuffled);

        // Each person gives to the next person in the circle
        for (int i = 0; i < shuffled.size(); i++) {
            Participant giver = shuffled.get(i);
            Participant receiver = shuffled.get((i + 1) % shuffled.size());

            Assignment assignment = Assignment.builder()
                    .event(event)
                    .giver(giver)
                    .receiver(receiver)
                    .build();
            assignments.add(assignment);
        }

        return assignments;
    }
}
