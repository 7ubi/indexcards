package com.x7ubi.indexcards.rest;

import com.x7ubi.indexcards.jwt.JwtUtils;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
import com.x7ubi.indexcards.response.MessageResponse;
import com.x7ubi.indexcards.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project")
public class ProjectRestController {
    Logger logger = LoggerFactory.getLogger(ProjectRestController.class);

    private final JwtUtils jwtUtils;

    private final ProjectService projectService;

    public ProjectRestController(JwtUtils jwtUtils, ProjectService projectService) {
        this.jwtUtils = jwtUtils;
        this.projectService = projectService;
    }

    @GetMapping("/projects")
    public ResponseEntity<?> getProjects(
        @RequestHeader("Authorization") String authorization
    ){
        try {
            logger.info("Getting projects from User");
            return ResponseEntity
                    .ok()
                    .body(projectService.getUserProjects(authorization));
        } catch (UsernameNotFoundException usernameNotFoundException) {
            logger.error(usernameNotFoundException.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(usernameNotFoundException.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProject(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CreateProjectRequest createProjectRequest
    ){
        try {
            logger.info("Creating Project");
            projectService.createProject(authorization, createProjectRequest);
            logger.info("Project created");
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("Project created"));
        } catch (UsernameNotFoundException usernameNotFoundException) {
            logger.error(usernameNotFoundException.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(usernameNotFoundException.getMessage()));
        }
    }
}
