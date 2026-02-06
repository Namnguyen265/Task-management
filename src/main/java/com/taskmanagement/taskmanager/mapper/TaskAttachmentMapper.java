package com.taskmanagement.taskmanager.mapper;

import com.taskmanagement.taskmanager.dto.response.TaskAttachmentResponse;
import com.taskmanagement.taskmanager.entity.TaskAttachment;

import java.time.ZoneId;

public class TaskAttachmentMapper {

    public static TaskAttachmentResponse toResponse(TaskAttachment entity) {
        return TaskAttachmentResponse.builder()
                .id(entity.getId())
                .taskId(entity.getTask().getId())
                .originalFileName(entity.getOriginalFilename())
                .fileType(entity.getFileType())
                .fileSize(entity.getFileSize())
                .createdAt(
                        entity.getCreatedAt()
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                )
                .downloadUrl("/api/attachments/" + entity.getId() + "/download")
                .build();
    }
}
