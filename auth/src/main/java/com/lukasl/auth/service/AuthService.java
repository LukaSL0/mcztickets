package com.lukasl.auth.service;

import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lukasl.auth.dto.CreateUserDto;
import com.lukasl.auth.dto.LoginDto;
import com.lukasl.auth.dto.RefreshTokenDto;
import com.lukasl.auth.dto.response.AuthResponseDto;
import com.lukasl.auth.dto.response.UserResponseDto;
import com.lukasl.auth.entity.RefreshToken;
import com.lukasl.auth.entity.User;
import com.lukasl.auth.exception.ConflictException;
import com.lukasl.auth.exception.ResourceNotFoundException;
import com.lukasl.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    // ========== AUTHENTICATION ==========

    @Transactional
    public AuthResponseDto login(LoginDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + dto.getEmail()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String accessToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtService.getExpirationInSeconds())
                .user(toUserResponseDto(user))
                .build();
    }

    @Transactional
    public AuthResponseDto register(CreateUserDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new ConflictException("Username already exists");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        User user = User.builder()
                .name(dto.getName())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(hashedPassword)
                .build();

        User savedUser = userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
        String accessToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtService.getExpirationInSeconds())
                .user(toUserResponseDto(savedUser))
                .build();
    }

    @Transactional
    public AuthResponseDto refreshToken(RefreshTokenDto dto) {
        RefreshToken refreshToken = refreshTokenService.findByToken(dto.getRefreshToken());
        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String newAccessToken = jwtService.generateToken(userDetails);

        return AuthResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtService.getExpirationInSeconds())
                .user(toUserResponseDto(user))
                .build();
    }

    @Transactional
    public void logout(UUID userId) {
        refreshTokenService.revokeUserTokens(userId);
    }

    // ========== HELPERS ==========

    private UserResponseDto toUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
