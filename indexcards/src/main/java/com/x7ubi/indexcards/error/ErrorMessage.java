package com.x7ubi.indexcards.error;

public class ErrorMessage {
    public static class Authentication {
        public static String USERNAME_EXITS = "username_exists";
    }

    public static class Project {
        public static String USERNAME_NOT_FOUND = "username_not_found";

        public static String PROJECT_NAME_EXISTS = "project_name_exists";

        public static String PROJECT_NAME_TOO_LONG = "project_name_too_long";

        public static String PROJECT_NOT_FOUND = "project_not_found";
    }

    public static class IndexCards {

        public static String PROJECT_NOT_FOUND = "project_not_found";

        public static String INDEX_CARD_NOT_FOUND = "indexcard_not_found";

        public static String INDEXCARD_QUESTION_TOO_LONG = "indexcard_question_too_long";

        public static String INDEXCARD_ANSWER_TOO_LONG = "indexcard_answer_too_long";
    }
}
