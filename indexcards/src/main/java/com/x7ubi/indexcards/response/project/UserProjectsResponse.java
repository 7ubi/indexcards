package com.x7ubi.indexcards.response.project;

import com.x7ubi.indexcards.response.common.MessageResponse;
import com.x7ubi.indexcards.response.common.ResultResponse;

import java.util.List;

public class UserProjectsResponse extends ResultResponse {

    List<ProjectResponse> projectResponses;

    public UserProjectsResponse() {}

    public UserProjectsResponse(boolean success, List<MessageResponse> errorMessages, List<ProjectResponse> projectResponses) {
        super(success, errorMessages);
        this.projectResponses = projectResponses;
    }

    public List<ProjectResponse> getProjectResponses() {
        return projectResponses;
    }

    public void setProjectResponses(List<ProjectResponse> projectResponses) {
        this.projectResponses = projectResponses;
    }
}
