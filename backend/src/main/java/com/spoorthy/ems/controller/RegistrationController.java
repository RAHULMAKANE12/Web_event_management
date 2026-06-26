package com.spoorthy.ems.controller;

import com.spoorthy.ems.dto.ApiResponse;
import com.spoorthy.ems.dto.EventRegistrationDTO;
import com.spoorthy.ems.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<ApiResponse<EventRegistrationDTO>> register(@RequestBody Map<String, Long> request) {
        Long eventId = request.get("eventId");
        Long userId = request.get("userId");
        if (eventId == null || userId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("eventId and userId are required"));
        }
        EventRegistrationDTO reg = registrationService.registerForEvent(eventId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful! Confirmation email sent.", reg));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<List<EventRegistrationDTO>>> getByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(ApiResponse.success(registrationService.getRegistrationsByEvent(eventId)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<EventRegistrationDTO>>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(registrationService.getRegistrationsByUser(userId)));
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkRegistration(
            @RequestParam Long eventId, @RequestParam Long userId) {
        boolean isRegistered = registrationService.isRegistered(eventId, userId);
        Long count = registrationService.getRegistrationCount(eventId);
        Map<String, Object> result = Map.of("isRegistered", isRegistered, "totalRegistrations", count);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
