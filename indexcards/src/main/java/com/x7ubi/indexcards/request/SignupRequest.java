package com.x7ubi.indexcards.request;

public class SignupRequest {
    private String username;
    private String firstname;
    private String surname;
    private String password;

    public SignupRequest(String username, String firstname, String surname, String password) {
        this.username = username;
        this.firstname = firstname;
        this.surname = surname;
        this.password = password;
    }

    public SignupRequest() {}

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
