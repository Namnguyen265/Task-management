package com.taskmanagement.taskmanager.mapper;

import com.taskmanagement.taskmanager.dto.response.UserResponse;
import com.taskmanagement.taskmanager.entity.User;

public class UserMapper {

    public static UserResponse toUserResponse(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        return dto;
    }
}
