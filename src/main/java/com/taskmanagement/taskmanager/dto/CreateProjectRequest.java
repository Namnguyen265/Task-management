package com.taskmanagement.taskmanager.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CreateProjectRequest {

    private String name;
    private String description;

    private Date startDate;
    private Date endDate;
}
