package com.mcztickets.payments.kafka.event;

public record UpdateOrderStatusEvent(
        Long orderId,
        String status) {
}
