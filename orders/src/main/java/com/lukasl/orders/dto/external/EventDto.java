package com.lukasl.orders.dto.external;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime eventDate;
    private String location;
    private Integer totalTickets;
    private Integer availableTickets;
    private BigDecimal basePrice;
}
