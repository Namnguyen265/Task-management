package com.taskmanagement.taskmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class TaskAttachmentResponse {

    private Long id;
    private Long taskId;

    private String originalFileName;
    private String fileType;
    private Long fileSize;

    private LocalDateTime createdAt;

    private String downloadUrl;
}
