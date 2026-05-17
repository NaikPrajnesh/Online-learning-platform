package com.elearn.platform.service;

import com.elearn.platform.dto.AuthRequest;
import com.elearn.platform.dto.AuthResponse;
import com.elearn.platform.model.User;
import com.elearn.platform.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponse signup(AuthRequest request) {
        if (request.getEmail() == null || request.getPassword() == null || request.getFullName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All fields are required");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        User user = new User();
        user.setEmail(request.getEmail().trim());
        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName().trim());
        user.setRole("STUDENT");
        user = userRepository.save(user);
        return new AuthResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole(), "Signup successful");
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!user.getPassword().equals(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        return new AuthResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole(), "Login successful");
    }
}
