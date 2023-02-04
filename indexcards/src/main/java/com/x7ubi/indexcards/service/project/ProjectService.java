package com.x7ubi.indexcards.service.project;

import com.x7ubi.indexcards.jwt.JwtUtils;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
import com.x7ubi.indexcards.response.indexcard.IndexCardResponse;
import com.x7ubi.indexcards.response.project.UserProjectResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
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

    public ArrayList<UserProjectResponse> getUserProjects(String authorization) {
        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        if(!userRepo.existsByUsername(username)){
            throw new UsernameNotFoundException("Username not found");
        }

        User user = this.userRepo.findByUsername(username).get();

        Project[] projects = new Project[user.getProjects().size()];
        projects = user.getProjects().toArray(projects);

        ArrayList<UserProjectResponse> userProjectResponses = new ArrayList<>();

        for(Project project: projects) {
            ArrayList<IndexCardResponse> indexCardResponses = new ArrayList<>();

            for(IndexCard indexCard: project.getIndexCards()) {
                indexCardResponses.add(new IndexCardResponse(indexCard.getName()));
            }

            userProjectResponses.add(new UserProjectResponse(project.getName(), indexCardResponses));
        }

        return userProjectResponses;
    }
}
