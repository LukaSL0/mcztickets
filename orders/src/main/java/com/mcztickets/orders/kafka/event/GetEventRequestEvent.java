package com.mcztickets.orders.kafka.event;

public record GetEventRequestEvent(
        String correlationId,
        Long eventId) {
}
