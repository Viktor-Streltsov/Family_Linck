package com.familylink.backend.auth.service;

import com.familylink.backend.auth.dto.RegisterRequest;
import com.familylink.backend.auth.dto.RegisterResponse;
import com.familylink.backend.auth.exception.EmailAlreadyExistsException;
import com.familylink.backend.user.User;
import com.familylink.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(hashedPassword)
                .name(request.getName().trim())
                .build();

        User savedUser = userRepository.save(user);

        return RegisterResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .createdAt(savedUser.getCreatedAt())
                .message("Регистрация прошла успешно")
                .build();
    }
}