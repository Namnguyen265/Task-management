package com.taskmanagement.taskmanager.dto.response;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;

    private Long createdById;
    private String createdByEmail;

    private Set<ProjectMemberResponse> members;
}
