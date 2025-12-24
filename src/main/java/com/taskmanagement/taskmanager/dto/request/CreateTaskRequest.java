package com.taskmanagement.taskmanager.dto.request;

import com.taskmanagement.taskmanager.enums.TaskPriority;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class CreateTaskRequest {
    private Long projectId;
    private String title;
    private String description;
    private TaskPriority priority;
    private Date startDate;
    private Date targetEndDate;
    private Long assignedTo;
}
