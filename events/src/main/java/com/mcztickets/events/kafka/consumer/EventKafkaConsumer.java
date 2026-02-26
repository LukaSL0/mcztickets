package com.mcztickets.events.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.mcztickets.events.dto.response.EventResponseDto;
import com.mcztickets.events.kafka.event.EventResponseEvent;
import com.mcztickets.events.kafka.event.GetEventRequestEvent;
import com.mcztickets.events.kafka.event.ReserveTicketsEvent;
import com.mcztickets.events.service.EventService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventKafkaConsumer {

    private final EventService eventService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "events.get.request", groupId = "events-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeGetEventRequest(GetEventRequestEvent event) {
        log.info("Received GetEventRequest for eventId={}, correlationId={}", event.eventId(), event.correlationId());
        try {
            EventResponseDto dto = eventService.getEventById(event.eventId());
            EventResponseEvent response = new EventResponseEvent(
                    event.correlationId(),
                    dto.id(),
                    dto.name(),
                    dto.description(),
                    dto.eventDate(),
                    dto.location(),
                    dto.totalTickets(),
                    dto.availableTickets(),
                    dto.basePrice());
            kafkaTemplate.send("events.get.response", event.correlationId(), response);
        } catch (Exception e) {
            log.error("Failed to process GetEventRequest for eventId={}: {}", event.eventId(), e.getMessage());
        }
    }

    @KafkaListener(topics = "events.reserve.tickets", groupId = "events-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeReserveTickets(ReserveTicketsEvent event) {
        log.info("Received ReserveTicketsEvent for eventId={}, tickets={}", event.eventId(), event.tickets());
        try {
            eventService.reserveTickets(event.eventId(), event.tickets());
        } catch (Exception e) {
            log.error("Failed to reserve tickets for eventId={}: {}", event.eventId(), e.getMessage());
        }
    }
}
