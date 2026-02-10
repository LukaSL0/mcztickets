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
import com.lukasl.events.dto.response.EventResponseDto;
import com.lukasl.events.service.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventsController {

    private final EventService eventService;
    
    @GetMapping()
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        List<EventResponseDto> allEvents = eventService.getAllEvents();
        return ResponseEntity.status(HttpStatus.OK).body(allEvents);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long eventId) {
        EventResponseDto event = eventService.getEventById(eventId);
        return ResponseEntity.status(HttpStatus.OK).body(event);
    }

    @PostMapping()
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody CreateEventDto dto) {
        EventResponseDto createdEvent = eventService.createEvent(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(createdEvent);
    }

    @PostMapping("/{eventId}/reserve")
    public ResponseEntity<EventResponseDto> reserveTickets(
        @PathVariable Long eventId,
        @Valid @RequestBody UpdateEventDto dto
    ) {
        EventResponseDto updatedEvent = eventService.reserveTickets(eventId, dto.getTickets());
        return ResponseEntity.status(HttpStatus.OK).body(updatedEvent);
    }
}
