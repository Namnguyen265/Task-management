package com.taskmanagement.taskmanager.dto;

import com.taskmanagement.taskmanager.enums.Role;
import com.taskmanagement.taskmanager.enums.Status;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private Status status;
}
