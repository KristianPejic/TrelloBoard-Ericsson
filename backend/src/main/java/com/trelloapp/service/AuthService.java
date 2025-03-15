package com.trelloapp.service;

import com.trelloapp.config.JwtTokenProvider;
import com.trelloapp.dto.AuthRequestDto;
import com.trelloapp.dto.AuthResponseDto;
import com.trelloapp.exception.UnauthorizedException;
import com.trelloapp.model.User;
import com.trelloapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * Service method for user registration
     * Uses Builder pattern for creating User entity
     */
    public AuthResponseDto register(AuthRequestDto request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Create new user using Builder pattern
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(User.Role.USER)
                .build();

        // Save user to database
        userRepository.save(user);

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user);

        // Return response using Builder pattern
        return AuthResponseDto.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }

    /**
     * Service method for user authentication
     * Uses Strategy pattern through AuthenticationManager
     */
    public AuthResponseDto authenticate(AuthRequestDto request) {
        try {
            // Use Spring Security's authentication mechanism
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));

            // Find user from database
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(user);

            // Return response DTO
            return AuthResponseDto.builder()
                    .token(token)
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .build();
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Invalid email or password");
        }
    }
}