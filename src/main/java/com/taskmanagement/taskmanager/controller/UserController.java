package com.taskmanagement.taskmanager.controller;

import com.taskmanagement.taskmanager.dto.response.UserResponse;
import com.taskmanagement.taskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/status")
    public UserResponse updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return userService.updateStatus(id, status);
    }
}
