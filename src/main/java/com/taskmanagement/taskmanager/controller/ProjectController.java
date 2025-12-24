package com.taskmanagement.taskmanager.controller;

import com.taskmanagement.taskmanager.dto.CreateProjectRequest;
import com.taskmanagement.taskmanager.dto.ProjectResponse;
import com.taskmanagement.taskmanager.dto.request.UpdateProjectMemberRoleRequest;
import com.taskmanagement.taskmanager.dto.request.UpdateProjectRequest;
import com.taskmanagement.taskmanager.entity.Project;
import com.taskmanagement.taskmanager.mapper.ProjectMapper;
import com.taskmanagement.taskmanager.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public Project createProject(@RequestBody CreateProjectRequest request) {
        return projectService.createProject(request);
    }

    @PostMapping("/{id}/members/{userId}")
    public void addMember(@PathVariable Long id, @PathVariable Long userId) {
        projectService.addMember(id, userId);
    }

    @PutMapping("/{id}/leader/{userId}")
    public void assignLeader(@PathVariable Long id, @PathVariable Long userId) {
        projectService.assignLeader(id, userId);
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    public void removeMember(
            @PathVariable Long projectId,
            @PathVariable Long userId
    ) {
        projectService.removeMember(projectId, userId);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getProjects(){
        return ResponseEntity.ok(projectService.getMyProjects());
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectDetail(@PathVariable Long projectId){
        return ResponseEntity.ok(projectService.getProjectDetail(projectId));
    }

    @PutMapping("/{id}")
    public ProjectResponse updateProject(
            @PathVariable Long id,
            @RequestBody UpdateProjectRequest request
    ) {
        Project project = projectService.updateProjectInfo(id, request);
        return ProjectMapper.toResponse(project);
    }

    @PutMapping("/{projectId}/members/{userId}/role")
    public ResponseEntity<Void> updateMemberRole(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @RequestBody UpdateProjectMemberRoleRequest request
    ) {
        projectService.updateMemberRole(
                projectId,
                userId,
                request.getRoleInProject()
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProjectResponse>> searchProjects(
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(projectService.searchProjects(keyword));
    }
}
