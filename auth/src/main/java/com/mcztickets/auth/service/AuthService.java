package com.mcztickets.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mcztickets.auth.dto.CreateUserDto;
import com.mcztickets.auth.dto.LoginDto;
import com.mcztickets.auth.dto.RefreshTokenDto;
import com.mcztickets.auth.dto.response.AuthResponseDto;
import com.mcztickets.auth.dto.response.UserResponseDto;
import com.mcztickets.auth.entity.RefreshToken;
import com.mcztickets.auth.entity.User;
import com.mcztickets.auth.exception.ConflictException;
import com.mcztickets.auth.exception.ResourceNotFoundException;
import com.mcztickets.auth.exception.UnauthorizedException;
import com.mcztickets.auth.repository.UserRepository;

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
    private final CustomUserDetailsService customUserDetailsService;

    public AuthResponseDto login(LoginDto dto) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );

        User user = userRepository.findByUsername(dto.username())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponseDto register(CreateUserDto dto) {
        if (userRepository.existsByUsername(dto.username())) {
            throw new ConflictException("Username already exists");
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new ConflictException("Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(dto.password());
        User user = User.builder()
                .name(dto.name())
                .username(dto.username())
                .email(dto.email())
                .password(hashedPassword)
                .build();

        User savedUser = userRepository.save(user);
        return buildAuthResponse(savedUser);
    }

    @Transactional
    public AuthResponseDto refreshToken(RefreshTokenDto dto) {
        RefreshToken refreshToken = refreshTokenService.findByToken(dto.refreshToken());
        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String newAccessToken = jwtService.generateToken(userDetails);

        return AuthResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtService.getExpirationInSeconds())
                .user(UserResponseDto.from(user))
                .build();
    }

    @Transactional
    public void logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            throw new UnauthorizedException("User is not authenticated");
        }

        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        refreshTokenService.revokeUserTokens(user.getId());
        customUserDetailsService.evictUserCache(username);
        SecurityContextHolder.clearContext();
    }

    private AuthResponseDto buildAuthResponse(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String accessToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtService.getExpirationInSeconds())
                .user(UserResponseDto.from(user))
                .build();
    }
}