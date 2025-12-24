package com.taskmanagement.taskmanager.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UpdateProjectRequest {
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
}
