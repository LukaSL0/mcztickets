package com.lukasl.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDto(
    @NotBlank(message = "Refresh token is required")
    String refreshToken
) {}