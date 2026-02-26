package com.mcztickets.events.kafka.event;

public record ReserveTicketsEvent(
        Long eventId,
        Integer tickets) {
}
