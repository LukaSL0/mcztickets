package com.mcztickets.auth.dto.response;

import java.util.UUID;

import com.mcztickets.auth.entity.User;
import com.mcztickets.auth.enums.UserRole;

import lombok.Builder;

@Builder
public record UserResponseDto(
    UUID id,
    String name,
    String username,
    String email,
    UserRole role
) {
    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
