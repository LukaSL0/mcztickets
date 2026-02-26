package com.mcztickets.events.kafka.event;

public record GetEventRequestEvent(
        String correlationId,
        Long eventId) {
}
