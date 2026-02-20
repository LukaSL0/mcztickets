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
        return eventRepository.findAll().stream()
                .map(EventResponseDto::from)
                .toList();
    }

    public EventResponseDto getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        
        return EventResponseDto.from(event);
    }

    @Transactional
    public EventResponseDto createEvent(CreateEventDto dto) {
        Event event = Event.builder()
                .name(dto.name())
                .description(dto.description())
                .eventDate(dto.eventDate())
                .location(dto.location())
                .totalTickets(dto.totalTickets())
                .availableTickets(dto.totalTickets())
                .basePrice(dto.basePrice())
                .build();

        Event savedEvent = eventRepository.save(event);

        return EventResponseDto.from(savedEvent);
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
        
        return EventResponseDto.from(updatedEvent);
    }

}
