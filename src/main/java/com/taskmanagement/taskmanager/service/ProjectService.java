package com.taskmanagement.taskmanager.service;

import com.taskmanagement.taskmanager.dto.CreateProjectRequest;
import com.taskmanagement.taskmanager.dto.ProjectResponse;
import com.taskmanagement.taskmanager.dto.request.UpdateProjectRequest;
import com.taskmanagement.taskmanager.entity.Project;
import com.taskmanagement.taskmanager.entity.ProjectMember;
import com.taskmanagement.taskmanager.entity.User;
import com.taskmanagement.taskmanager.enums.Role;
import com.taskmanagement.taskmanager.enums.RoleInProject;
import com.taskmanagement.taskmanager.mapper.ProjectMapper;
import com.taskmanagement.taskmanager.repository.ProjectMemberRepository;
import com.taskmanagement.taskmanager.repository.ProjectRepository;
import com.taskmanagement.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public Project createProject(CreateProjectRequest request){
        User admin = authService.getCurrentUser();

        if (admin.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admin can create project");
        }

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setCreatedBy(admin);

        project = projectRepository.save(project);

        // Admin mặc định là LEADER
        ProjectMember leader = new ProjectMember();
        leader.setProject(project);
        leader.setUser(admin);
        leader.setRoleInProject(RoleInProject.LEADER);

        projectMemberRepository.save(leader);

        return project;
    }

//    public void addMember(Long projectId, Long userId) {
//
//        User currentUser = authService.getCurrentUser();
//
//        Project project = projectRepository.findById(projectId)
//                .orElseThrow(() -> new RuntimeException("Project not found"));
//
//        ProjectMember myRole = projectMemberRepository
//                .findByProjectIdAndUserId(projectId, currentUser.getId())
//                .orElseThrow(() -> new RuntimeException("Not project member"));
//
//        if (myRole.getRoleInProject() == RoleInProject.MEMBER &&
//                currentUser.getRole() != Role.ADMIN) {
//            throw new RuntimeException("Permission denied");
//        }
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        ProjectMember member = new ProjectMember();
//        member.setProject(project);
//        member.setUser(user);
//        member.setRoleInProject(RoleInProject.MEMBER);
//
//        projectMemberRepository.save(member);
//    }

    public void addMember(Long projectId, Long userId) {

        User currentUser = authService.getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        ProjectMember myRole = null;

        if (!isAdmin) {
            myRole = projectMemberRepository
                    .findByProjectIdAndUserId(projectId, currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Not project member"));
        }

        boolean isLeader = myRole != null && myRole.getRoleInProject() == RoleInProject.LEADER;

        if (!(isAdmin || isLeader)) {
            throw new RuntimeException("Permission denied");
        }

        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new RuntimeException("User already in project");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        member.setRoleInProject(RoleInProject.MEMBER);

        projectMemberRepository.save(member);
    }

//    public void addMembers(Long projectId, AddMembersRequest request) {
//
//        User currentUser = authService.getCurrentUser();
//
//        Project project = projectRepository.findById(projectId)
//                .orElseThrow(() -> new RuntimeException("Project not found"));
//
//        // Lấy role của người đang thao tác trong project
//        ProjectMember myRole = projectMemberRepository
//                .findByProjectIdAndUserId(projectId, currentUser.getId())
//                .orElseThrow(() -> new RuntimeException("Not a project member"));
//
//        // Chỉ ADMIN hoặc LEADER mới được add member
//        if (myRole.getRoleInProject() == RoleInProject.MEMBER
//                && currentUser.getRole() != Role.ADMIN) {
//            throw new RuntimeException("Permission denied");
//        }
//
//        for (Long userId : request.getUserIds()) {
//
//            // Không add trùng
//            boolean exists = projectMemberRepository
//                    .existsByProjectIdAndUserId(projectId, userId);
//
//            if (exists) continue;
//
//            User user = userRepository.findById(userId)
//                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));
//
//            ProjectMember member = new ProjectMember();
//            member.setProject(project);
//            member.setUser(user);
//            member.setRoleInProject(RoleInProject.MEMBER);
//
//            projectMemberRepository.save(member);
//        }
//    }



    public void assignLeader(Long projectId, Long userId){
        User admin = authService.getCurrentUser();

        if (admin.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admin can assign leader");
        }

        ProjectMember member = projectMemberRepository
                .findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("User not in project"));

        member.setRoleInProject(RoleInProject.LEADER);
        projectMemberRepository.save(member);
    }

    public void removeMember(Long projectId, Long userId) {

        User currentUser = authService.getCurrentUser();

        // Project tồn tại
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Vai trò của người đang thao tác
        ProjectMember myRole = projectMemberRepository
                .findByProjectIdAndUserId(projectId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("You are not a project member"));

        // Member cần bị xóa
        ProjectMember target = projectMemberRepository
                .findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("User not in project"));

        // ❌ Không cho xóa chính mình
        if (currentUser.getId().equals(userId)) {
            throw new RuntimeException("Cannot remove yourself");
        }

        // ADMIN → OK
        if (currentUser.getRole() == Role.ADMIN) {
            projectMemberRepository.delete(target);
            return;
        }

        // LEADER → chỉ xóa MEMBER
        if (myRole.getRoleInProject() == RoleInProject.LEADER) {

            if (target.getRoleInProject() == RoleInProject.LEADER) {
                throw new RuntimeException("Leader cannot remove another leader");
            }

            projectMemberRepository.delete(target);
            return;
        }

        // MEMBER → ❌
        throw new RuntimeException("Permission denied ssss");
    }

    public List<ProjectResponse> getMyProjects(){

        User currentUser = authService.getCurrentUser();

        List<Project> projects;

        if (currentUser.getRole() == Role.ADMIN){
            projects = projectRepository.findAll();
        } else {
            projects = projectRepository.findProjectsByUserId(currentUser.getId());
        }

        return projects.stream().map(ProjectMapper::toResponse).toList();
    }

    public ProjectResponse getProjectDetail(Long projectId){
        User currentUser = authService.getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found"
                ));

        boolean isMember = projectMemberRepository
                .existsByProjectIdAndUserId(projectId, currentUser.getId());

        if (!isMember && currentUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to view this project"
            );
        }

        return ProjectMapper.toResponse(project);
    }


    public Project updateProjectInfo(Long projectId, UpdateProjectRequest request) {

        User currentUser = authService.getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Nếu không phải ADMIN thì phải là LEADER của project
        if (currentUser.getRole() != Role.ADMIN) {

            ProjectMember member = projectMemberRepository
                    .findByProjectIdAndUserId(projectId, currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Not a project member"));

            if (member.getRoleInProject() != RoleInProject.LEADER) {
                throw new RuntimeException("Permission denied");
            }
        }

        // Update info
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());

        return projectRepository.save(project);
    }

    public void updateMemberRole(
            Long projectId,
            Long targetUserId,
            RoleInProject newRole
    ) {

        User currentUser = authService.getCurrentUser();

        // 1. Check project tồn tại
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // 2. Role của người thao tác
        ProjectMember myRole = projectMemberRepository
                .findByProjectIdAndUserId(projectId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("You are not project member"));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isLeader = myRole.getRoleInProject() == RoleInProject.LEADER;

        // 3. Check quyền
        if (!isAdmin && !isLeader) {
            throw new RuntimeException("Permission denied");
        }

        // 4. Member bị đổi role
        ProjectMember targetMember = projectMemberRepository
                .findByProjectIdAndUserId(projectId, targetUserId)
                .orElseThrow(() -> new RuntimeException("User not in project"));

        // 5. Update role (KHÔNG ràng buộc leader cuối)
        targetMember.setRoleInProject(newRole);
        projectMemberRepository.save(targetMember);
    }

    public List<ProjectResponse> searchProjects(String keyword) {

        List<Project> projects;

        if (keyword == null || keyword.trim().isEmpty()) {
            projects = projectRepository.findAll();
        } else {
            projects = projectRepository.findByNameContainingIgnoreCase(keyword);
        }

        return projects.stream()
                .map(ProjectMapper::toResponse)
                .toList();
    }



}
