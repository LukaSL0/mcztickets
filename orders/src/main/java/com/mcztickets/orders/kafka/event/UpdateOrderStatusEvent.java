package com.mcztickets.orders.kafka.event;

public record UpdateOrderStatusEvent(
        Long orderId,
        String status) {
}
