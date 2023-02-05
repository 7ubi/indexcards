package com.x7ubi.indexcards.error;

public class ErrorMessage {
    public static class Authentication {
        public static String USERNAME_EXITS = "Username already exists!";
    }

    public static class Project {
        public static String USERNAME_NOT_FOUND = "Username not found";

        public static String PROJECT_NAME_EXISTS = "A project with this name already exists";
    }
}
