package com.spoorthy.ems.controller;

import com.spoorthy.ems.dto.ApiResponse;
import com.spoorthy.ems.dto.EventDTO;
import com.spoorthy.ems.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventDTO>>> getEvents(
            @RequestParam(value = "clubId", required = false) Long clubId,
            @RequestParam(value = "status", required = false) String status) {

        List<EventDTO> events;
        if (clubId != null && status != null) {
            events = eventService.getEventsByClubAndStatus(clubId, status);
        } else if (clubId != null) {
            events = eventService.getEventsByClub(clubId);
        } else if (status != null && status.equalsIgnoreCase("UPCOMING")) {
            events = eventService.getUpcomingEvents();
        } else {
            events = eventService.getAllEvents();
        }
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getUpcomingEvents() {
        return ResponseEntity.ok(ApiResponse.success(eventService.getUpcomingEvents()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDTO>> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(eventService.getEventById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EventDTO>> createEvent(
            @RequestBody EventDTO eventDTO,
            @RequestParam(value = "createdById", required = false) Long createdById) {
        EventDTO created = eventService.createEvent(eventDTO, createdById);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Event created!", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDTO>> updateEvent(@PathVariable Long id, @RequestBody EventDTO eventDTO) {
        return ResponseEntity.ok(ApiResponse.success("Event updated!", eventService.updateEvent(id, eventDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok(ApiResponse.success("Event deleted!", null));
    }
}
