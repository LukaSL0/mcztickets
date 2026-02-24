package com.mcztickets.auth.dto;

import com.mcztickets.auth.enums.UserRole;

import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleDto(
    @NotNull(message = "Role is required")
    UserRole role
) {}
