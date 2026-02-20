package com.lukasl.events.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.lukasl.events.entity.Event;

import lombok.Builder;

@Builder
public record EventResponseDto(
    Long id,
    String name,
    String description,
    LocalDateTime eventDate,
    String location,
    Integer totalTickets,
    Integer availableTickets,
    BigDecimal basePrice
) {
    public static EventResponseDto from(Event event) {
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
