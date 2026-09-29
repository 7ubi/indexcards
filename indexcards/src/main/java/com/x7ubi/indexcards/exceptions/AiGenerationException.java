package com.x7ubi.indexcards.exceptions;

import org.springframework.http.HttpStatus;

/**
 * An AI card generation error. The message is an error code from {@code ErrorMessage.Ai} (translated in the frontend
 * as {@code backend.<code>}). Never use 401 here: the frontend logs the user out on 401, and a rejected Gemini key
 * says nothing about the user's session.
 */
public class AiGenerationException extends Exception {

    private final HttpStatus status;

    public AiGenerationException(String code, HttpStatus status) {
        super(code);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
