package com.mcztickets.orders.kafka.event;

public record ReserveTicketsEvent(
        Long eventId,
        Integer tickets) {
}
