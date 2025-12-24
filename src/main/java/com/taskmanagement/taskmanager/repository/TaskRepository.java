package com.taskmanagement.taskmanager.repository;

import com.taskmanagement.taskmanager.entity.Task;
import com.taskmanagement.taskmanager.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    List<Task> findByAssignedToId(Long userId);

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByTitleContainingIgnoreCase(String title);
}
