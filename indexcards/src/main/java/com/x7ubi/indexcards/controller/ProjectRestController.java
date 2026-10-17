package com.x7ubi.indexcards.controller;

import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.models.SecurityUser;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
import com.x7ubi.indexcards.response.project.ProjectResponse;
import com.x7ubi.indexcards.service.project.ArchiveProjectService;
import com.x7ubi.indexcards.service.project.CreateProjectService;
import com.x7ubi.indexcards.service.project.DeleteProjectService;
import com.x7ubi.indexcards.service.project.EditProjectService;
import com.x7ubi.indexcards.service.project.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project")
public class ProjectRestController {
    Logger logger = LoggerFactory.getLogger(ProjectRestController.class);

    private final ProjectService projectService;

    private final CreateProjectService createProjectService;

    private final DeleteProjectService deleteProjectService;

    private final EditProjectService editProjectService;

    private final ArchiveProjectService archiveProjectService;

    public ProjectRestController(
            ProjectService projectService,
            CreateProjectService createProjectService,
            DeleteProjectService deleteProjectService, EditProjectService editProjectService,
            ArchiveProjectService archiveProjectService) {
        this.projectService = projectService;
        this.createProjectService = createProjectService;
        this.deleteProjectService = deleteProjectService;
        this.editProjectService = editProjectService;
        this.archiveProjectService = archiveProjectService;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ProjectResponse>> getProjects(
            @AuthenticationPrincipal SecurityUser securityUser
    ) throws EntityNotFoundException {
        logger.info("Getting projects from User");
        String username = securityUser.getUsername();

        List<ProjectResponse> response = projectService.getUserProjects(username);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ProjectResponse> getProject(
            @AuthenticationPrincipal SecurityUser securityUser, @PathVariable Long id) throws EntityNotFoundException, UnauthorizedException {
        logger.info("Getting project");
        String username = securityUser.getUsername();

        ProjectResponse response = projectService.getProject(username, id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createProject(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody CreateProjectRequest createProjectRequest
    ) throws EntityNotFoundException, EntityCreationException {
        logger.info("Creating Project");
        String username = securityUser.getUsername();

        createProjectService.createProject(username, createProjectRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteProject(@AuthenticationPrincipal SecurityUser securityUser, @RequestParam Long id) throws EntityNotFoundException, UnauthorizedException {
        logger.info("Deleting Project");
        String username = securityUser.getUsername();

        deleteProjectService.deleteProject(username, id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("")
    public ResponseEntity<?> editProject(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestParam Long id,
            @RequestBody CreateProjectRequest createProjectRequest
    ) throws EntityNotFoundException, EntityCreationException, UnauthorizedException {
        logger.info("Editing Project");
        String username = securityUser.getUsername();

        editProjectService.editProject(createProjectRequest, id, username);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<?> archiveProject(
            @AuthenticationPrincipal SecurityUser securityUser, @PathVariable Long id
    ) throws EntityNotFoundException, UnauthorizedException {
        logger.info("Archiving Project");
        String username = securityUser.getUsername();

        archiveProjectService.archiveProject(username, id);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{id}/unarchive")
    public ResponseEntity<?> unarchiveProject(
            @AuthenticationPrincipal SecurityUser securityUser, @PathVariable Long id
    ) throws EntityNotFoundException, UnauthorizedException {
        logger.info("Unarchiving Project");
        String username = securityUser.getUsername();

        archiveProjectService.unarchiveProject(username, id);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
