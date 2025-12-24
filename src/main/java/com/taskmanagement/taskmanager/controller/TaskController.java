package com.taskmanagement.taskmanager.controller;

import com.taskmanagement.taskmanager.dto.request.CreateTaskRequest;
import com.taskmanagement.taskmanager.dto.request.UpdateTaskRequest;
import com.taskmanagement.taskmanager.dto.response.TaskResponse;
import com.taskmanagement.taskmanager.entity.Task;
import com.taskmanagement.taskmanager.mapper.TaskMapper;
import com.taskmanagement.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(request));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskResponse>> getTasksByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskRequest request
    ) {
        Task task = taskService.updateTask(taskId, request);
        return ResponseEntity.ok(TaskMapper.toResponse(task));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build(); // 204
    }
}
