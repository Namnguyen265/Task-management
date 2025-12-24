package com.taskmanagement.taskmanager.dto.request;

import com.taskmanagement.taskmanager.enums.RoleInProject;
import lombok.Data;

@Data
public class UpdateProjectMemberRoleRequest {
    private RoleInProject roleInProject;
}
