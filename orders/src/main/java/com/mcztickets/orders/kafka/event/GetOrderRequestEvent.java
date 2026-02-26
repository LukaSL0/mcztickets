package com.mcztickets.orders.kafka.event;

public record GetOrderRequestEvent(
        String correlationId,
        Long orderId) {
}
