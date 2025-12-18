package com.secretsanta.backend.service;

import com.secretsanta.backend.dto.request.AddParticipantRequest;
import com.secretsanta.backend.dto.response.ParticipantResponse;
import com.secretsanta.backend.exception.BadRequestException;
import com.secretsanta.backend.exception.ConflictException;
import com.secretsanta.backend.exception.ResourceNotFoundException;
import com.secretsanta.backend.model.Event;
import com.secretsanta.backend.model.Participant;
import com.secretsanta.backend.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private EventService eventService;

    /**
     * Add participant to event
     */
    @Transactional
    public ParticipantResponse addParticipant(Long eventId, AddParticipantRequest request) {
        Event event = eventService.findEventByIdAndVerifyOwnership(eventId);

        // Check if event is locked
        if (!event.canBeModified()) {
            throw new BadRequestException("Cannot add participants to locked event");
        }

        // Check if email already exists in this event
        if (participantRepository.existsByEventIdAndEmail(eventId, request.getEmail())) {
            throw new ConflictException(
                    "Email already exists in this event: " + request.getEmail()
            );
        }

        // Create participant
        Participant participant = Participant.builder()
                .name(request.getName())
                .email(request.getEmail())
                .event(event)
                .build();

        Participant savedParticipant = participantRepository.save(participant);
        return ParticipantResponse.from(savedParticipant);
    }

    /**
     * Get all participants for an event
     */
    @Transactional(readOnly = true)
    public List<ParticipantResponse> getParticipants(Long eventId) {
        // Verify ownership
        eventService.findEventByIdAndVerifyOwnership(eventId);

        List<Participant> participants = participantRepository
                .findByEventIdOrderByCreatedAtAsc(eventId);

        return participants.stream()
                .map(ParticipantResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Remove participant from event
     * Only allowed if event is not locked
     */
    @Transactional
    public void removeParticipant(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Participant", "id", participantId
                ));

        // Verify admin owns the event
        eventService.findEventByIdAndVerifyOwnership(participant.getEvent().getId());

        // Check if event is locked
        if (!participant.getEvent().canBeModified()) {
            throw new BadRequestException("Cannot remove participants from locked event");
        }

        participantRepository.delete(participant);
    }

    /**
     * Get all participants for an event (internal use)
     */
    @Transactional(readOnly = true)
    public List<Participant> getParticipantEntities(Long eventId) {
        return participantRepository.findByEventIdOrderByCreatedAtAsc(eventId);
    }
}
