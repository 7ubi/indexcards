package com.x7ubi.indexcards.response.common;

import java.util.List;

public class ResultResponse {

    private boolean success;

    private List<MessageResponse> errorMessages;

    public ResultResponse() {}

    public ResultResponse(boolean success, List<MessageResponse> errorMessages) {
        this.success = success;
        this.errorMessages = errorMessages;
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<MessageResponse> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<MessageResponse> errorMessages) {
        this.errorMessages = errorMessages;
    }
}
