package com.lukasl.auth.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lukasl.auth.dto.UpdateUserRoleDto;
import com.lukasl.auth.dto.response.UserResponseDto;
import com.lukasl.auth.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping()
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> allUsers = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(allUsers);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID userId) {
        UserResponseDto user = userService.getUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<UserResponseDto> updateUserRole(
        @PathVariable UUID userId,
        @Valid @RequestBody UpdateUserRoleDto dto
    ) {
        UserResponseDto updatedUser = userService.updateUserRole(userId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }
}
