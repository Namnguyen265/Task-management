package com.taskmanagement.taskmanager.repository;

import com.taskmanagement.taskmanager.entity.TaskAttachment;

import java.util.List;

public interface TaskAttachmentRepository {
    List<TaskAttachment> findByTaskId(Long taskId);
}
