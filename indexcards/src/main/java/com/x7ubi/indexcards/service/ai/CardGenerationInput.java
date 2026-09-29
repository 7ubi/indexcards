package com.x7ubi.indexcards.service.ai;

/**
 * @param notes     stripped notes, empty if none
 * @param pdf       raw PDF bytes or null
 * @param cardCount maximum number of cards (already clamped)
 */
public record CardGenerationInput(String notes, byte[] pdf, int cardCount) {
}
