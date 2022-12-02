package com.x7ubi.indexcards.rest;

import com.x7ubi.indexcards.jwt.JwtUtils;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
import com.x7ubi.indexcards.response.MessageResponse;
import com.x7ubi.indexcards.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        logger.info(jwtUtils.getUsernameFromAuthorizationHeader(authorization));
        return null;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProject(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CreateProjectRequest createProjectRequest
    ){
        try {
            projectService.createProject(authorization, createProjectRequest);
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("Project created"));
        } catch (UsernameNotFoundException usernameNotFoundException) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(usernameNotFoundException.getMessage()));
        }
    }
}
