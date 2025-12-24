package com.taskmanagement.taskmanager.dto.request;

import com.taskmanagement.taskmanager.enums.TaskPriority;
import com.taskmanagement.taskmanager.enums.TaskStatus;
import lombok.Data;

import java.util.Date;

@Data
public class UpdateTaskRequest {
    private String title;
    private String description;
    private TaskPriority priority;
    private Date targetEndDate;
    private Long assignedTo;
    private TaskStatus status;
}
