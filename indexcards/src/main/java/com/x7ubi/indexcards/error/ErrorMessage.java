package com.x7ubi.indexcards.error;

public class ErrorMessage {
    public static class Authentication {
        public static String USERNAME_EXITS = "Username already exists!";
    }

    public static class Project {
        public static String USERNAME_NOT_FOUND = "Username not found";

        public static String PROJECT_NAME_EXISTS = "A project with this name already exists";

        public static String PROJECT_NAME_TOO_LONG
            = "The name of the project was too long, only 100 characters are allowed";
    }

    public static class IndexCards {

        public static String PROJECT_NOT_FOUND = "Project not found";

        public static String INDEXCARD_QUESTION_TOO_LONG
                = "The question of the index card was too long, only 500 characters are allowed";


        public static String INDEXCARD_ANSWER_TOO_LONG
                = "The answer of the index card was too long, only 500 characters are allowed";
    }
}
