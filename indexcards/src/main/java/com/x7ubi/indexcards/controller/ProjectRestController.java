package com.x7ubi.indexcards.controller;

import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.jwt.JwtUtils;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
import com.x7ubi.indexcards.response.project.ProjectResponse;
import com.x7ubi.indexcards.service.project.CreateProjectService;
import com.x7ubi.indexcards.service.project.DeleteProjectService;
import com.x7ubi.indexcards.service.project.EditProjectService;
import com.x7ubi.indexcards.service.project.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project")
public class ProjectRestController {
    Logger logger = LoggerFactory.getLogger(ProjectRestController.class);

    private final JwtUtils jwtUtils;

    private final ProjectService projectService;

    private final CreateProjectService createProjectService;

    private final DeleteProjectService deleteProjectService;

    private final EditProjectService editProjectService;

    public ProjectRestController(
            JwtUtils jwtUtils,
            ProjectService projectService,
            CreateProjectService createProjectService,
            DeleteProjectService deleteProjectService, EditProjectService editProjectService) {
        this.jwtUtils = jwtUtils;
        this.projectService = projectService;
        this.createProjectService = createProjectService;
        this.deleteProjectService = deleteProjectService;
        this.editProjectService = editProjectService;
    }

    @GetMapping("/projects")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ProjectResponse>> getProjects(
            @RequestHeader("Authorization") String authorization
    ) throws EntityNotFoundException {
        logger.info("Getting projects from User");
        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        List<ProjectResponse> response = projectService.getUserProjects(username);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/project")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ProjectResponse> getProject(
            @RequestHeader("Authorization") String authorization, @RequestParam Long id) throws EntityNotFoundException, UnauthorizedException {
        logger.info("Getting project");
        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        ProjectResponse response = projectService.getProject(username, id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createProject(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CreateProjectRequest createProjectRequest
    ) throws EntityNotFoundException, EntityCreationException {
        logger.info("Creating Project");
        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        createProjectService.createProject(username, createProjectRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteProject(@RequestHeader("Authorization") String authorization, @RequestParam Long id) throws EntityNotFoundException {
        logger.info("Deleting Project");
        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        deleteProjectService.deleteProject(username, id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editProject(
            @RequestHeader("Authorization") String authorization,
            @RequestParam Long id,
            @RequestBody CreateProjectRequest createProjectRequest
    ) throws EntityNotFoundException, EntityCreationException {
        logger.info("Editing Project");
        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        editProjectService.editProject(createProjectRequest, id, username);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
