package com.taskmanagement.taskmanager.controller;

import com.taskmanagement.taskmanager.dto.response.AuthResponse;
import com.taskmanagement.taskmanager.dto.request.LoginRequest;
import com.taskmanagement.taskmanager.dto.request.RegisterRequest;
import com.taskmanagement.taskmanager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(){
        return ResponseEntity.ok("Logout successfully");
    }
}
