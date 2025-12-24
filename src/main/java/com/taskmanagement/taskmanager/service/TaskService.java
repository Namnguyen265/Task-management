package com.taskmanagement.taskmanager.service;

import com.taskmanagement.taskmanager.dto.request.CreateTaskRequest;
import com.taskmanagement.taskmanager.dto.request.UpdateTaskRequest;
import com.taskmanagement.taskmanager.dto.response.TaskResponse;
import com.taskmanagement.taskmanager.entity.Project;
import com.taskmanagement.taskmanager.entity.ProjectMember;
import com.taskmanagement.taskmanager.entity.Task;
import com.taskmanagement.taskmanager.entity.User;
import com.taskmanagement.taskmanager.enums.Role;
import com.taskmanagement.taskmanager.enums.RoleInProject;
import com.taskmanagement.taskmanager.enums.TaskStatus;
import com.taskmanagement.taskmanager.exception.ForbiddenException;
import com.taskmanagement.taskmanager.mapper.TaskMapper;
import com.taskmanagement.taskmanager.repository.ProjectMemberRepository;
import com.taskmanagement.taskmanager.repository.ProjectRepository;
import com.taskmanagement.taskmanager.repository.TaskRepository;
import com.taskmanagement.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public TaskResponse createTask(CreateTaskRequest request){
        User currentUser = authService.getCurrentUser();

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // check quyền ADMIN hoặc LEADER
//        project.validateLeaderOrAdmin(currentUser);

        ProjectMember myRole = projectMemberRepository.findByProjectIdAndUserId(project.getId(), currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Not project member"));

        if (myRole.getRoleInProject() == RoleInProject.MEMBER &&
                currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Permission denied");
        }

        Task task = new Task();
        task.setProject(project);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStartDate(request.getStartDate());
        task.setTargetEndDate(request.getTargetEndDate());
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedBy(currentUser);

        if (request.getAssignedTo() != null) {
            User assignee = userRepository.findById(request.getAssignedTo())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            task.setAssignedTo(assignee);
        }

        return TaskMapper.toResponse(taskRepository.save(task));
    }

    public List<TaskResponse> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    public Task updateTask1(Long taskId, UpdateTaskRequest request) {

        User currentUser = authService.getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        boolean isLeader = projectMemberRepository.existsByProjectAndUserAndRole(
                task.getProject().getId(),
                currentUser.getId(),
                RoleInProject.LEADER
        );

        boolean isAssignedToMe =
                task.getAssignedTo() != null &&
                        task.getAssignedTo().getId().equals(currentUser.getId());

        if (isAdmin) {
            updateTaskInfo(task, request);
            return taskRepository.save(task);
        }

        // =========================
        // TASK CỦA CHÍNH MÌNH (LEADER / MEMBER)
        // =========================
        if (isAssignedToMe) {
            updateTaskInfo(task, request);
            handleStatusChange(task, request.getStatus());
            return taskRepository.save(task);
        }

        // =========================
        // LEADER update task của người khác (không được update status)
        // =========================
        if (isLeader) {
            updateTaskInfo(task, request);
            return taskRepository.save(task);
        }

        throw new RuntimeException("Permission denied");

    }

    public Task updateTask(Long taskId, UpdateTaskRequest request) {
        log.info("Đang bắt đầu update task có ID: {}", taskId);
        User currentUser = authService.getCurrentUser();
        log.debug("User thực hiện: {}, Role: {}", currentUser.getName(), currentUser.getRole());

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        boolean isLeader = projectMemberRepository
                .existsByProjectAndUserAndRole(
                        task.getProject().getId(),
                        currentUser.getId(),
                        RoleInProject.LEADER
                );

        boolean isOwnerTask = task.getAssignedTo() != null &&
                task.getAssignedTo().getId().equals(currentUser.getId());

        // =========================
        // ADMIN
        // =========================
        if (isAdmin) {

            if (request.getStatus() != null) {
                throw new ForbiddenException("Admin cannot update task status");
            }

            updateCommonFields(task, request);
            return taskRepository.save(task);
        }

        // =========================
        // LEADER
        // =========================
        if (isLeader) {

            if (isOwnerTask) {
                // leader + task của mình → full quyền
                updateCommonFields(task, request);
                handleStatusChange(task, request.getStatus());
                return taskRepository.save(task);
            }

            // leader + task người khác
            if (request.getStatus() != null) {
                throw new ForbiddenException("Leader cannot update status of others' tasks");
            }

            updateCommonFields(task, request);
            return taskRepository.save(task);
        }

        // =========================
        // MEMBER
        // =========================
        if (isOwnerTask) {
            // member chỉ được update task của mình
            updateCommonFields(task, request);
            handleStatusChange(task, request.getStatus());
            return taskRepository.save(task);
        }

        throw new ForbiddenException("Permission denied");
    }

    public Task updateTask3(Long taskId, UpdateTaskRequest request) {

        User currentUser = authService.getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        boolean isLeader = projectMemberRepository
                .existsByProjectAndUserAndRole(
                        task.getProject().getId(),
                        currentUser.getId(),
                        RoleInProject.LEADER
                );

        boolean isOwnerTask = task.getAssignedTo() != null
                && task.getAssignedTo().getId().equals(currentUser.getId());

        // =========================
        // ADMIN
        // =========================
        if (isAdmin) {

            if (request.getStatus() != null) {
                throw new AccessDeniedException("Admin cannot update task status");
            }

            updateCommonFields(task, request);
            return taskRepository.save(task);
        }

        // =========================
        // LEADER
        // =========================
        if (isLeader) {

            if (!isOwnerTask && request.getStatus() != null) {
                throw new AccessDeniedException(
                        "Leader cannot update status of others' tasks"
                );
            }

            updateCommonFields(task, request);
            handleStatusChange(task, request.getStatus());
            return taskRepository.save(task);
        }

        // =========================
        // MEMBER
        // =========================
        if (isOwnerTask) {

            updateCommonFields(task, request);
            handleStatusChange(task, request.getStatus());
            return taskRepository.save(task);
        }

        // =========================
        // NO PERMISSION
        // =========================
        throw new AccessDeniedException("Permission denied");
    }

    public void deleteTask(Long taskId){
        User currentUser = authService.getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        boolean isLeader = projectMemberRepository
                .existsByProjectAndUserAndRole(
                        task.getProject().getId(),
                        currentUser.getId(),
                        RoleInProject.LEADER
                );

        boolean isOwnerTask = task.getAssignedTo() != null &&
                task.getAssignedTo().getId().equals(currentUser.getId());

        // =========================
        // PERMISSION CHECK
        // =========================

        // ADMIN → full quyền
        if (isAdmin) {
            taskRepository.delete(task);
            return;
        }

        // LEADER → xóa mọi task trong project
        if (isLeader) {
            taskRepository.delete(task);
            return;
        }

        // MEMBER → chỉ xóa task của mình
        if (isOwnerTask) {
            taskRepository.delete(task);
            return;
        }

        throw new RuntimeException("You do not have permission to delete this task");
    }



    private void handleStatusChange(Task task, TaskStatus newStatus) {

        if (newStatus == TaskStatus.DONE) {
            task.setCompleteDate(new Date());
        } else {
            task.setCompleteDate(null);
        }

        task.setStatus(newStatus);
    }



    private void updateTaskInfo(Task task, UpdateTaskRequest request) {

        if (request.getTitle() != null)
            task.setTitle(request.getTitle());

        if (request.getDescription() != null)
            task.setDescription(request.getDescription());

        if (request.getPriority() != null)
            task.setPriority(request.getPriority());

        if (request.getTargetEndDate() != null)
            task.setTargetEndDate(request.getTargetEndDate());

        if (request.getAssignedTo() != null) {
            User assignee = userRepository.findById(request.getAssignedTo())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            task.setAssignedTo(assignee);
        }
    }

    private void updateCommonFields(Task task, UpdateTaskRequest request) {

        if (request.getTitle() != null)
            task.setTitle(request.getTitle());

        if (request.getDescription() != null)
            task.setDescription(request.getDescription());

        if (request.getPriority() != null)
            task.setPriority(request.getPriority());

        if (request.getTargetEndDate() != null)
            task.setTargetEndDate(request.getTargetEndDate());

        if (request.getAssignedTo() != null) {
            User assignee = userRepository.findById(request.getAssignedTo())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            task.setAssignedTo(assignee);
        }
    }


}
