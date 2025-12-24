package com.taskmanagement.taskmanager.dto.request;

import com.taskmanagement.taskmanager.enums.TaskStatus;
import lombok.Data;

@Data
public class UpdateTaskStatusRequest {
    private TaskStatus status;
}
