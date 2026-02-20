package com.lukasl.events.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateEventDto(
    @NotNull(message = "Tickets are required")
    Integer tickets
) {}