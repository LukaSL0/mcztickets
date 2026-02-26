package com.mcztickets.orders.kafka.event;

import java.math.BigDecimal;

public record OrderResponseEvent(
        String correlationId,
        Long id,
        Long eventId,
        String status,
        BigDecimal amount) {
}
