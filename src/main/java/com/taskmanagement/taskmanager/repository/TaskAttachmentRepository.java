package com.taskmanagement.taskmanager.repository;

import com.taskmanagement.taskmanager.entity.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {
    List<TaskAttachment> findByTaskId(Long taskId);

    Optional<TaskAttachment> findById(Long id );
}
