package com.taskmanagement.taskmanager.entity;

import com.taskmanagement.taskmanager.enums.Role;
import com.taskmanagement.taskmanager.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // ADMIN, EMPLOYEE

    @Enumerated(EnumType.STRING)
    private Status status; // ACTIVE, INACTIVE

    private Date startDate;
    private Date endDate;

    @Column(updatable = false)
    private Timestamp createdAt;

    private Timestamp updatedAt;

}

