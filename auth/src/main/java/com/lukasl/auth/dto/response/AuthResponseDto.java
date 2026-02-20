package com.lukasl.auth.dto.response;

import lombok.Builder;

@Builder
public record AuthResponseDto(
    String accessToken,
    String refreshToken,
    String tokenType,
    Long expiresIn,
    UserResponseDto user
) {
    public static class AuthResponseDtoBuilder {
        private String tokenType = "Bearer";
    }
}