package com.x7ubi.indexcards.repository;

import com.x7ubi.indexcards.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectRepo extends JpaRepository<Project, Long> {

    List<Project> findProjectByName(String name);

    Project findProjectByProjectId(Long projectId);

    Boolean existsByProjectId(Long projectId);

    void deleteProjectByProjectId(Long projectId);

    @Query("select p from Project p where p.archived = false and p.examDate < :today "
            + "and (p.autoArchivedExamDate is null or p.autoArchivedExamDate <> p.examDate)")
    List<Project> findAutoArchiveCandidates(LocalDate today);
}
