package com.x7ubi.indexcards.service.ai;

/**
 * One card suggestion as returned by the LLM.
 */
public record GeneratedCard(String question, String answer) {
}
