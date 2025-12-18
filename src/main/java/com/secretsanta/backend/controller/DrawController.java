package com.secretsanta.backend.controller;

import com.secretsanta.backend.dto.request.DrawRequest;
import com.secretsanta.backend.dto.response.DrawResponse;
import com.secretsanta.backend.service.DrawService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class DrawController {

    @Autowired
    private DrawService drawService;

    /**
     * POST /api/events/{eventId}/draw
     * Perform the Secret Santa draw
     */
    @PostMapping("/{eventId}/draw")
    public ResponseEntity<DrawResponse> performDraw(
            @PathVariable Long eventId,
            @RequestBody DrawRequest request
    ) {
        // Set default if request is null
        if (request == null) {
            request = DrawRequest.builder().allowRedraw(false).build();
        }

        DrawResponse response = drawService.performDraw(eventId, request);
        return ResponseEntity.ok(response);
    }
}
