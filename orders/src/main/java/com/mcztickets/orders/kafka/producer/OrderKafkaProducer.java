package com.mcztickets.orders.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.mcztickets.orders.kafka.event.GetEventRequestEvent;
import com.mcztickets.orders.kafka.event.ReserveTicketsEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendGetEventRequest(GetEventRequestEvent event) {
        log.info("Sending GetEventRequest for eventId={}, correlationId={}",
                event.eventId(), event.correlationId());
        kafkaTemplate.send("events.get.request", event.correlationId(), event);
    }

    public void sendReserveTickets(ReserveTicketsEvent event) {
        log.info("Sending ReserveTickets for eventId={}, tickets={}",
                event.eventId(), event.tickets());
        kafkaTemplate.send("events.reserve.tickets", String.valueOf(event.eventId()), event);
    }
}
