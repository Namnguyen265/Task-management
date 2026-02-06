package com.taskmanagement.taskmanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "task_attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    // tên file gốc (hiển thị cho user)
    private String originalFilename;

    // tên file lưu trong server
    private String storedFilename;

    private String fileType;
    private Long fileSize;

    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @Column(updatable = false)
    private Timestamp createdAt;
}
