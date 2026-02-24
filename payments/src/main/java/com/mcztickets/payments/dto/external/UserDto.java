package com.mcztickets.payments.dto.external;

import java.util.UUID;

public record UserDto(
    UUID id,
    String name
) {}