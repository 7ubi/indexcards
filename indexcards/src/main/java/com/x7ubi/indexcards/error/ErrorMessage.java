package com.x7ubi.indexcards.error;

public class ErrorMessage {

    public static String INTERNAL_SERVER_ERROR = "Internal server error";

    public static final String FILE_TOO_LARGE = "file_too_large";

    public static class Authentication {
        public static final String BAD_CREDENTIALS = "bad_credentials";
        public static final String FORBIDDEN = "forbidden";
        public static String USERNAME_EXITS = "username_exists";
    }

    public static class User {
        public static final String WRONG_PASSWORD = "wrong_password";

        public static final String USERNAME_EMPTY = "username_empty";

        public static final String USERNAME_TOO_LONG = "username_too_long";

        public static final String USERNAME_UNCHANGED = "username_unchanged";

        public static final String PASSWORD_EMPTY = "password_empty";
    }

    public static class Project {
        public static final String USER_NOT_PROJECT_OWNER = "user_not_project_owner";

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

        public static final String PROJECT_ARCHIVED = "project_archived";

        public static final String INDEXCARD_EMPTY = "indexcard_empty";

        public static final String TOO_MANY_INDEXCARDS = "too_many_indexcards";
    }

    public static class Images {

        public static String INVALID_IMAGE = "invalid_image";

        public static String IMAGE_NOT_FOUND = "image_not_found";

        public static final String IMAGE_TOO_LARGE = "image_too_large";
    }

    public static class Ai {
        public static final String DISABLED = "ai_generation_disabled";
        public static final String API_KEY_MISSING = "ai_api_key_missing";
        public static final String API_KEY_INVALID = "ai_api_key_invalid";
        public static final String API_KEY_FORBIDDEN = "ai_api_key_forbidden";
        public static final String API_KEY_UNREADABLE = "ai_api_key_unreadable";
        public static final String BILLING = "ai_billing_error";
        public static final String RATE_LIMITED = "ai_rate_limited";
        public static final String IN_PROGRESS = "ai_generation_in_progress";
        public static final String INPUT_EMPTY = "ai_input_empty";
        public static final String NOTES_TOO_LONG = "ai_notes_too_long";
        public static final String PDF_INVALID = "ai_pdf_invalid";
        public static final String PDF_TOO_LARGE = "ai_pdf_too_large";
        public static final String PDF_TOO_MANY_PAGES = "ai_pdf_too_many_pages";
        public static final String REFUSED = "ai_generation_refused";
        public static final String OUTPUT_TRUNCATED = "ai_output_truncated";
        public static final String NO_CARDS = "ai_no_cards";
        public static final String TIMEOUT = "ai_generation_timeout";
        public static final String UNAVAILABLE = "ai_generation_unavailable";
        public static final String FAILED = "ai_generation_failed";
    }
}
