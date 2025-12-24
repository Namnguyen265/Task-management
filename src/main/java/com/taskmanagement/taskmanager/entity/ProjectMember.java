package com.taskmanagement.taskmanager.entity;

import com.taskmanagement.taskmanager.enums.RoleInProject;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "project_members", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"project_id", "user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private RoleInProject roleInProject = RoleInProject.MEMBER;

    @Column(updatable = false)
    private java.sql.Timestamp createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectMember)) return false;
        ProjectMember that = (ProjectMember) o;
        return project != null && user != null &&
                project.getId().equals(that.project.getId()) &&
                user.getId().equals(that.user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(project.getId(), user.getId());
    }
}


