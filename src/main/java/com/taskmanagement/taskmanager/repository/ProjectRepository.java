package com.taskmanagement.taskmanager.repository;

import com.taskmanagement.taskmanager.entity.Project;
import com.taskmanagement.taskmanager.entity.ProjectMember;
import com.taskmanagement.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {



//    List<ProjectMember> findByProjectId(Long projectId);

//    List<ProjectMember> findByUserId(Long userId);

//    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);

//    boolean existsByProjectIdAndUserId(Long projectId, Long userId);

    @Query("""
        select pm.project 
        from ProjectMember pm 
        where pm.user.id = :userId
    """)
    List<Project> findProjectsByUserId(Long userId);

    List<Project> findByNameContainingIgnoreCase(String name);
}
