package com.lukasl.auth.dto;

import com.lukasl.auth.enums.UserRole;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRoleDto {

    @NotNull(message = "Role is required")
    private UserRole role;

}
