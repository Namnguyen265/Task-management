package com.taskmanagement.taskmanager.repository;

import com.taskmanagement.taskmanager.entity.ProjectMember;
import com.taskmanagement.taskmanager.enums.RoleInProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);

    List<ProjectMember> findByProjectId(Long projectId);

    boolean existsByProjectIdAndUserId(Long projectId, Long userId);

    List<ProjectMember> findByUserId(Long userId);

    void deleteByProjectIdAndUserId(Long projectId, Long userId);

    @Query("""
    SELECT COUNT(pm) > 0
    FROM ProjectMember pm
    WHERE pm.project.id = :projectId
      AND pm.user.id = :userId
      AND pm.roleInProject = :role
""")
    boolean existsByProjectAndUserAndRole(
            @Param("projectId") Long projectId,
            @Param("userId") Long userId,
            @Param("role") RoleInProject role
    );
}
