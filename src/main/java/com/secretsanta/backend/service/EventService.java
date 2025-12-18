package com.secretsanta.backend.service;

import com.secretsanta.backend.dto.request.CreateEventRequest;
import com.secretsanta.backend.dto.request.LockEventRequest;
import com.secretsanta.backend.dto.request.UpdateEventRequest;
import com.secretsanta.backend.dto.response.EventDetailResponse;
import com.secretsanta.backend.dto.response.EventResponse;
import com.secretsanta.backend.exception.BadRequestException;
import com.secretsanta.backend.exception.ResourceNotFoundException;
import com.secretsanta.backend.model.Admin;
import com.secretsanta.backend.model.Event;
import com.secretsanta.backend.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AuthService authService;

    /**
     * Get all events for current admin
     */
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        Admin currentAdmin = authService.getCurrentAdmin();
        List<Event> events = eventRepository.findByAdminIdOrderByCreatedAtDesc(
                currentAdmin.getId()
        );
        return events.stream()
                .map(EventResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Create a new event
     */
    @Transactional
    public EventResponse createEvent(CreateEventRequest request) {
        Admin currentAdmin = authService.getCurrentAdmin();

        Event event = Event.builder()
                .name(request.getName())
                .description(request.getDescription())
                .drawDate(request.getDrawDate())
                .budget(request.getBudget())
                .isLocked(false)
                .admin(currentAdmin)
                .build();

        Event savedEvent = eventRepository.save(event);
        return EventResponse.from(savedEvent);
    }

    /**
     * Get event details by ID
     * Includes participants
     */
    @Transactional(readOnly = true)
    public EventDetailResponse getEventById(Long eventId) {
        Event event = findEventByIdAndVerifyOwnership(eventId);
        return EventDetailResponse.from(event);
    }

    /**
     * Update event details
     * Only allowed if event is not locked
     */
    @Transactional
    public EventResponse updateEvent(Long eventId, UpdateEventRequest request) {
        Event event = findEventByIdAndVerifyOwnership(eventId);

        // Check if event can be modified
        if (!event.canBeModified()) {
            throw new BadRequestException("Cannot update locked event");
        }

        // Update fields if provided
        if (request.getName() != null) {
            event.setName(request.getName());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getDrawDate() != null) {
            event.setDrawDate(request.getDrawDate());
        }
        if (request.getBudget() != null) {
            event.setBudget(request.getBudget());
        }

        Event updatedEvent = eventRepository.save(event);
        return EventResponse.from(updatedEvent);
    }

    /**
     * Delete event
     * Cannot delete locked event with assignments
     */
    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = findEventByIdAndVerifyOwnership(eventId);

        // Check if event can be deleted
        if (event.getIsLocked() && event.hasDrawBeenPerformed()) {
            throw new BadRequestException(
                    "Cannot delete locked event with assignments. Unlock first."
            );
        }

        eventRepository.delete(event);
    }

    /**
     * Lock or unlock event
     */
    @Transactional
    public Map<String, Object> lockEvent(Long eventId, LockEventRequest request) {
        Event event = findEventByIdAndVerifyOwnership(eventId);

        // If locking, verify draw has been performed
        if (request.getLocked() && !event.hasDrawBeenPerformed()) {
            throw new BadRequestException("Cannot lock event without performing draw");
        }

        event.setIsLocked(request.getLocked());
        eventRepository.save(event);

        return Map.of(
                "id", event.getId(),
                "isLocked", event.getIsLocked(),
                "message", event.getIsLocked() ?
                        "Event locked successfully" : "Event unlocked successfully"
        );
    }

    /**
     * Find event by ID and verify current admin owns it
     */
    @Transactional(readOnly = true)
    public Event findEventByIdAndVerifyOwnership(Long eventId) {
        Admin currentAdmin = authService.getCurrentAdmin();
        return eventRepository.findByIdAndAdminId(eventId, currentAdmin.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Event", "id", eventId
                ));
    }

    /**
     * Check if event is locked
     */
    @Transactional(readOnly = true)
    public boolean isEventLocked(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        return event.getIsLocked();
    }
}
