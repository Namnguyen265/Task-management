package com.taskmanagement.taskmanager.mapper;

import com.taskmanagement.taskmanager.dto.response.TaskResponse;
import com.taskmanagement.taskmanager.entity.Task;

public class TaskMapper {

    public static TaskResponse toResponse(Task task) {
        TaskResponse res = new TaskResponse();

        res.setId(task.getId());
        res.setTitle(task.getTitle());
        res.setDescription(task.getDescription());
        res.setPriority(task.getPriority());
        res.setStatus(task.getStatus());

        res.setStartDate(task.getStartDate());
        res.setTargetEndDate(task.getTargetEndDate());
        res.setCompleteDate(task.getCompleteDate());

        res.setProjectId(task.getProject().getId());

        res.setCreatedById(task.getCreatedBy().getId());
        res.setCreatedByEmail(task.getCreatedBy().getEmail());

        if (task.getAssignedTo() != null) {
            res.setAssignedToId(task.getAssignedTo().getId());
            res.setAssignedToEmail(task.getAssignedTo().getEmail());
        }

        return res;
    }
}
