package com.x7ubi.indexcards.repository;

import com.x7ubi.indexcards.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepo extends JpaRepository<Project, Long> {

    List<Project> findProjectByName(String name);

    Project findProjectByProjectId(Long projectId);

    Boolean existsByProjectId(Long projectId);

    void deleteProjectByProjectId(Long projectId);
}
