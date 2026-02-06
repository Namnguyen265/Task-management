package com.taskmanagement.taskmanager.service;

import com.taskmanagement.taskmanager.dto.response.UserResponse;
import com.taskmanagement.taskmanager.entity.User;
import com.taskmanagement.taskmanager.enums.Status;
import com.taskmanagement.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToResponse(user);
    }

    public UserResponse updateStatus(Long id, String status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(Status.valueOf(status.toUpperCase()));
        userRepository.save(user);

        return convertToResponse(user);
    }

    private UserResponse convertToResponse(User user) {
        UserResponse dto = new UserResponse();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}
