package com.taskmanagement.taskmanager.dto;

import lombok.Data;

@Data
public class ProjectMemberResponse {
    private Long userId;
    private String name;
    private String email;
    private String roleInProject;
}
