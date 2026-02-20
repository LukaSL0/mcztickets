package com.lukasl.orders.dto.external;

import java.util.UUID;

public record UserDto(
    UUID id,
    String name,
    String username,
    String email,
    String role
){}
