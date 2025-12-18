package com.secretsanta.backend.controller;

import com.secretsanta.backend.dto.request.CreateEventRequest;
import com.secretsanta.backend.dto.request.LockEventRequest;
import com.secretsanta.backend.dto.request.UpdateEventRequest;
import com.secretsanta.backend.dto.response.EventDetailResponse;
import com.secretsanta.backend.dto.response.EventResponse;
import com.secretsanta.backend.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    /**
     * GET /api/events
     * Get all events for authenticated admin
     */
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        List<EventResponse> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    /**
     * POST /api/events
     * Create a new event
     */
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request
    ) {
        EventResponse response = eventService.createEvent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET /api/events/{id}
     * Get event details with participants
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventDetailResponse> getEventById(@PathVariable Long id) {
        EventDetailResponse response = eventService.getEventById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/events/{id}
     * Update event details
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventRequest request
    ) {
        EventResponse response = eventService.updateEvent(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/events/{id}
     * Delete an event
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/events/{id}/lock
     * Lock or unlock an event
     */
    @PutMapping("/{id}/lock")
    public ResponseEntity<Map<String, Object>> lockEvent(
            @PathVariable Long id,
            @Valid @RequestBody LockEventRequest request
    ) {
        Map<String, Object> response = eventService.lockEvent(id, request);
        return ResponseEntity.ok(response);
    }
}
