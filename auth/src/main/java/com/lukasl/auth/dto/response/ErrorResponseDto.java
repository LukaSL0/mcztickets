package com.lukasl.auth.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponseDto(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    Map<String, String> fieldErrors
) {}
