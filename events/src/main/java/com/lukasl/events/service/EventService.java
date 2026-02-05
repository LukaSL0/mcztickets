package com.lukasl.events.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lukasl.events.dto.CreateEventDto;
import com.lukasl.events.dto.response.EventResponseDto;
import com.lukasl.events.entity.Event;
import com.lukasl.events.exception.ConflictException;
import com.lukasl.events.exception.ResourceNotFoundException;
import com.lukasl.events.repository.EventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<EventResponseDto> getAllEvents() {
        return toListResponseDto(eventRepository.findAll());
    }

    public EventResponseDto getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        
        return toResponseDto(event);
    }

    @Transactional
    public EventResponseDto createEvent(CreateEventDto dto) {
        Event event = Event.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .location(dto.getLocation())
                .totalTickets(dto.getTotalTickets())
                .availableTickets(dto.getTotalTickets())
                .basePrice(dto.getBasePrice())
                .build();

        Event savedEvent = eventRepository.save(event);

        return toResponseDto(savedEvent);
    }

    @Transactional
    public EventResponseDto reserveTickets(Long eventId, int tickets) {
        int updatedRows = eventRepository.reserveTickets(eventId, tickets);

        if (updatedRows == 0) {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
            
            throw new ConflictException("Not enough tickets available. Available: " + 
                event.getAvailableTickets() + ", Requested: " + tickets);
        }

        Event updatedEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        
        return toResponseDto(updatedEvent);
    }

    // ========== HELPERS ==========

    private List<EventResponseDto> toListResponseDto(List<Event> events) {
        return events.stream()
        .map(this::toResponseDto)
        .toList();
    }

    private EventResponseDto toResponseDto(Event event) {
        return EventResponseDto.builder()
            .id(event.getId())
            .name(event.getName())
            .description(event.getDescription())
            .eventDate(event.getEventDate())
            .location(event.getLocation())
            .totalTickets(event.getTotalTickets())
            .availableTickets(event.getAvailableTickets())
            .basePrice(event.getBasePrice())
            .build();
    }

}
