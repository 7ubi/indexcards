package com.x7ubi.indexcards.request.user;

/**
 * The user types their username to confirm the irreversible deletion.
 */
public class DeleteAccountRequest {

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
