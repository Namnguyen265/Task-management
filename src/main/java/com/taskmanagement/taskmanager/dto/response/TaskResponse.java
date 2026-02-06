package com.taskmanagement.taskmanager.dto.response;

import com.taskmanagement.taskmanager.enums.TaskPriority;
import com.taskmanagement.taskmanager.enums.TaskStatus;
import lombok.Data;

import java.util.Date;

@Data
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskPriority priority;
    private TaskStatus status;

    private Date startDate;
    private Date targetEndDate;
    private Date completeDate;

    private Long projectId;

    private Long createdById;
    private String createdByEmail;

    private Long assignedToId;
    private String assignedToEmail;
}
