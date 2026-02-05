package com.lukasl.events.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lukasl.events.dto.CreateEventDto;
import com.lukasl.events.dto.UpdateEventDto;
import com.lukasl.events.dto.response.EventDto;
import com.lukasl.events.service.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventsController {

    private final EventService eventService;
    
    @GetMapping()
    public ResponseEntity<List<EventDto>> getAllEvents() {
        List<EventDto> allEvents = eventService.getAllEvents();
        return ResponseEntity.ok(allEvents);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEventById(@PathVariable Long eventId) {
        EventDto event = eventService.getEventById(eventId);
        return ResponseEntity.ok(event);
    }

    @PostMapping()
    public ResponseEntity<EventDto> createEvent(@Valid @RequestBody CreateEventDto dto) {
        EventDto createdEvent = eventService.createEvent(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(createdEvent);
    }

    @PostMapping("/{eventId}/reserve")
    public ResponseEntity<EventDto> reserveTickets(
        @PathVariable Long eventId,
        @Valid @RequestBody UpdateEventDto dto
    ) {
        EventDto updatedEvent = eventService.reserveTickets(eventId, dto.getTickets());
        return ResponseEntity.ok(updatedEvent);
    }
}
