package com.x7ubi.indexcards.service.project;

import com.x7ubi.indexcards.jwt.JwtUtils;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class CreateProjectService {

    private final ProjectRepo projectRepo;

    private final UserRepo userRepo;

    private final JwtUtils jwtUtils;

    public CreateProjectService(ProjectRepo projectRepo, UserRepo userRepo, JwtUtils jwtUtils) {
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

        List<Project> userProjects = user.getProjects();
        userProjects.add(project);

        user.setProjects(userProjects);
    }
}
