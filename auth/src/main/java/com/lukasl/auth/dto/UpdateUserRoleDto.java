package com.lukasl.auth.dto;

import com.lukasl.auth.enums.UserRole;

import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleDto(
    @NotNull(message = "Role is required")
    UserRole role
) {}
