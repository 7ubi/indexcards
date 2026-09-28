package com.x7ubi.indexcards.response.user;

public class UserResponse {

    private String username;

    private String firstname;

    private String surname;

    public UserResponse() {}

    public UserResponse(String username, String firstname, String surname) {
        this.username = username;
        this.firstname = firstname;
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
