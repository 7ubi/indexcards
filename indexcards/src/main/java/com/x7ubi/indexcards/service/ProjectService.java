package com.x7ubi.indexcards.service;

import com.x7ubi.indexcards.jwt.JwtUtils;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
import com.x7ubi.indexcards.response.UserProjectResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;

@Service
public class ProjectService {

    private final ProjectRepo projectRepo;

    private final UserRepo userRepo;

    private final JwtUtils jwtUtils;

    public ProjectService(ProjectRepo projectRepo, UserRepo userRepo, JwtUtils jwtUtils) {
        this.projectRepo = projectRepo;
        this.userRepo = userRepo;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public void createProject(String authorization, CreateProjectRequest createProjectRequest)
            throws UsernameNotFoundException {
        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        if(!userRepo.existsByUsername(username)){
            throw new UsernameNotFoundException("Username not found");
        }

        User user = this.userRepo.findByUsername(username).get();

        Project project = new Project();

        project.setName(createProjectRequest.getName());

        this.projectRepo.save(project);

        Set<Project> userProjects = user.getProjects();
        userProjects.add(project);

        user.setProjects(userProjects);
    }

    public UserProjectResponse[] getUserProjects(String authorization) {
        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        if(!userRepo.existsByUsername(username)){
            throw new UsernameNotFoundException("Username not found");
        }

        User user = this.userRepo.findByUsername(username).get();

        Project[] projects = new Project[user.getProjects().size()];
        projects = user.getProjects().toArray(projects);

        UserProjectResponse[] userProjectResponses = new UserProjectResponse[projects.length];

        for(int i = 0; i < projects.length; i++) {
            userProjectResponses[i] = new UserProjectResponse(projects[i].getName());
        }

        return userProjectResponses;
    }
}
