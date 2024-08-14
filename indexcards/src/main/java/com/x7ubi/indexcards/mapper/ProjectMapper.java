package com.x7ubi.indexcards.mapper;

import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
import com.x7ubi.indexcards.response.project.ProjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {IndexCardMapper.class}
)
public interface ProjectMapper {

    List<ProjectResponse> mapToResponse(List<Project> projects);

    @Mapping(source = "indexCards", target = "indexCardResponses")
    ProjectResponse mapToResponse(Project project);

    Project mapRequestToProject(CreateProjectRequest createProjectRequest);
}
