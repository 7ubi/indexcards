package com.x7ubi.indexcards.controller;

import com.x7ubi.indexcards.jwt.JwtUtils;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
import com.x7ubi.indexcards.response.common.ResultResponse;
import com.x7ubi.indexcards.response.project.UserProjectResponse;
import com.x7ubi.indexcards.response.project.UserProjectsResponse;
import com.x7ubi.indexcards.service.project.CreateProjectService;
import com.x7ubi.indexcards.service.project.DeleteProjectService;
import com.x7ubi.indexcards.service.project.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project")
public class ProjectRestController {
    Logger logger = LoggerFactory.getLogger(ProjectRestController.class);

    private final JwtUtils jwtUtils;

    private final ProjectService projectService;

    private final CreateProjectService createProjectService;

    private final DeleteProjectService deleteProjectService;

    public ProjectRestController(
        JwtUtils jwtUtils,
        ProjectService projectService,
        CreateProjectService createProjectService,
        DeleteProjectService deleteProjectService) {
        this.jwtUtils = jwtUtils;
        this.projectService = projectService;
        this.createProjectService = createProjectService;
        this.deleteProjectService = deleteProjectService;
    }

    @GetMapping("/projects")
    public ResponseEntity<?> getProjects(
        @RequestHeader("Authorization") String authorization
    ){
        logger.info("Getting projects from User");
        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        UserProjectsResponse response = projectService.getUserProjects(username);

        if(response.isSuccess()) {
            return ResponseEntity.ok().body(response);
        }

        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("project")
    public ResponseEntity<?> getProject(
            @RequestParam Long id
    ) {
        logger.info("Getting project");

        UserProjectResponse response = projectService.getProject(id);

        if(response.isSuccess()) {
            return ResponseEntity.ok().body(response);
        }

        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProject(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CreateProjectRequest createProjectRequest
    ){
        logger.info("Creating Project");
        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        ResultResponse result = createProjectService.createProject(username, createProjectRequest);

        if(result.isSuccess()) {
            return ResponseEntity.ok().body(result);
        }

        return ResponseEntity.badRequest().body(result);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProject(
            @RequestHeader("Authorization") String authorization,
            @RequestParam Long id
    ) {
        logger.info("Deleting Project");
        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        ResultResponse result = deleteProjectService.deleteProject(username, id);

        if(result.isSuccess()) {
            return ResponseEntity.ok().body(result);
        }

        return ResponseEntity.badRequest().body(result);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editProject(
            @RequestParam Long id,
            @RequestBody CreateProjectRequest createProjectRequest
    ) {
        logger.info("Editing Project");
        ResultResponse result = projectService.editProject(createProjectRequest, id);

        if(result.isSuccess()) {
            return ResponseEntity.ok().body(result);
        }

        return ResponseEntity.badRequest().body(result);
    }
}
