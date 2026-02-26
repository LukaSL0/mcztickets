package com.mcztickets.orders.kafka.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponseEvent(
        String correlationId,
        Long id,
        String name,
        String description,
        LocalDateTime eventDate,
        String location,
        Integer totalTickets,
        Integer availableTickets,
        BigDecimal basePrice) {
}
