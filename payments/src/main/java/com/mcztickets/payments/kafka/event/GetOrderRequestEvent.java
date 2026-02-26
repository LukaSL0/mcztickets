package com.mcztickets.payments.kafka.event;

public record GetOrderRequestEvent(
        String correlationId,
        Long orderId) {
}
