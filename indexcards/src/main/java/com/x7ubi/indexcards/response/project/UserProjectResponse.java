package com.x7ubi.indexcards.response.project;

import com.x7ubi.indexcards.response.common.ResultResponse;

public class UserProjectResponse extends ResultResponse {
    ProjectResponse projectResponse;

    public UserProjectResponse() {
    }

    public UserProjectResponse(ProjectResponse projectResponse) {
        this.projectResponse = projectResponse;
    }

    public ProjectResponse getProjectResponse() {
        return projectResponse;
    }

    public void setProjectResponse(ProjectResponse projectResponse) {
        this.projectResponse = projectResponse;
    }
}
