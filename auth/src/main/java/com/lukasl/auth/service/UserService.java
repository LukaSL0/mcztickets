package com.lukasl.auth.service;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lukasl.auth.dto.UpdateUserRoleDto;
import com.lukasl.auth.dto.response.UserResponseDto;
import com.lukasl.auth.entity.User;
import com.lukasl.auth.exception.ResourceNotFoundException;
import com.lukasl.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDto::from)
                .toList();
    }

    public UserResponseDto getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserResponseDto.from(user);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public UserResponseDto updateUserRole(UUID userId, UpdateUserRoleDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRole(dto.role());
        User savedUser = userRepository.save(user);

        return UserResponseDto.from(savedUser);
    }
}
