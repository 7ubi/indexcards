package com.x7ubi.indexcards.repository;

import com.x7ubi.indexcards.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepo extends JpaRepository<Project, Long> {

    Project findProjectByName(String name);

    Project findProjectByProjectId(Long projectId);

    Boolean existsByName(String name);

    Boolean existsByProjectId(Long projectId);
}
