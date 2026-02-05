package com.lukasl.orders.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.lukasl.orders.dto.external.EventDto;
import com.lukasl.orders.dto.external.UpdateEventDto;

@FeignClient(
    name = "event-service",
    url = "http://localhost:8082"
)
public interface EventClient {
    
    @GetMapping("/events/{eventId}")
    EventDto getEventById(
        @PathVariable Long eventId
    );

    @PostMapping("/events/{eventId}/reserve")
    void reserveTickets(
        @PathVariable Long eventId,
        @RequestBody UpdateEventDto dto
    );

}
