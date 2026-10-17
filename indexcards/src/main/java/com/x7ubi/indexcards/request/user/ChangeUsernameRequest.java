package com.x7ubi.indexcards.request.user;

public class ChangeUsernameRequest {

    private String username;

    public ChangeUsernameRequest() {}

    public ChangeUsernameRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
