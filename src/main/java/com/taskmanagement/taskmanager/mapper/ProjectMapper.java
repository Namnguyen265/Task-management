package com.taskmanagement.taskmanager.mapper;

import com.taskmanagement.taskmanager.dto.response.ProjectMemberResponse;
import com.taskmanagement.taskmanager.dto.response.ProjectResponse;
import com.taskmanagement.taskmanager.entity.Project;
import com.taskmanagement.taskmanager.entity.ProjectMember;
import com.taskmanagement.taskmanager.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProjectMapper {

    public static ProjectResponse toResponse(Project project) {
        ProjectResponse response = new ProjectResponse();

        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setStartDate(project.getStartDate());
        response.setEndDate(project.getEndDate());

        response.setCreatedById(project.getCreatedBy().getId());
        response.setCreatedByEmail(project.getCreatedBy().getEmail());

        response.setMembers(
                project.getMembers().stream()
                        .map(ProjectMapper::toProjectMemberResponse)
                        .collect(Collectors.toSet())
        );

        return response;
    }

    public static ProjectMemberResponse toProjectMemberResponse(ProjectMember pm) {
        User user = pm.getUser();

        ProjectMemberResponse res = new ProjectMemberResponse();
        res.setUserId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setRoleInProject(pm.getRoleInProject().name());

        return res;
    }

}
