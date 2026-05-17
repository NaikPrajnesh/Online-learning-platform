package com.elearn.platform.controller;

import com.elearn.platform.dto.AuthRequest;
import com.elearn.platform.dto.AuthResponse;
import com.elearn.platform.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public AuthResponse signup(@RequestBody AuthRequest request, HttpSession session) {
        AuthResponse response = authService.signup(request);
        session.setAttribute("userId", response.getUserId());
        session.setAttribute("role", response.getRole());
        return response;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request, HttpSession session) {
        AuthResponse response = authService.login(request);
        session.setAttribute("userId", response.getUserId());
        session.setAttribute("role", response.getRole());
        return response;
    }

    @PostMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }

    @GetMapping("/me")
    public AuthResponse me(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return new AuthResponse(null, null, null, null, "Not authenticated");
        }
        String role = (String) session.getAttribute("role");
        return new AuthResponse(userId, null, null, role, "Authenticated");
    }
}
