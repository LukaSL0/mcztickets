package com.lukasl.auth.dto.response;

import java.util.UUID;

import com.lukasl.auth.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private UUID id;
    private String name;
    private String username;
    private String email;
    private UserRole role;

}
