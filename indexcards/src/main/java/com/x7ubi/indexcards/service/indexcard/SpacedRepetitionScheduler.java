package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.models.Assessment;
import com.x7ubi.indexcards.models.IndexCard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * SM-2 inspired spaced-repetition scheduling, adapted to the app's 3-grade BAD/OK/GOOD rating
 * scale instead of SM-2's usual 0-5 quality score. Computes the next ease factor, repetition
 * count, interval and due date for a card based on how it was just assessed.
 */
public final class SpacedRepetitionScheduler {

    private static final double MIN_EASE_FACTOR = 1.3;

    private SpacedRepetitionScheduler() {
    }

    public static void schedule(IndexCard indexCard, Assessment assessment, LocalDateTime now, LocalDate examDate) {
        int repetitions = indexCard.getRepetitions();
        double easeFactor = indexCard.getEaseFactor();
        int previousIntervalDays = indexCard.getIntervalDays();

        int newRepetitions;
        double newEaseFactor;
        int newIntervalDays;

        switch (assessment) {
            case OK:
                newRepetitions = repetitions + 1;
                newEaseFactor = Math.max(MIN_EASE_FACTOR, easeFactor - 0.05);
                newIntervalDays = nextInterval(newRepetitions, previousIntervalDays, newEaseFactor, 1, 3, 0.8);
                break;
            case GOOD:
                newRepetitions = repetitions + 1;
                newEaseFactor = easeFactor + 0.1;
                newIntervalDays = nextInterval(newRepetitions, previousIntervalDays, newEaseFactor, 1, 6, 1.0);
                break;
            case BAD:
            case UNRATED:
            default:
                newRepetitions = 0;
                newEaseFactor = Math.max(MIN_EASE_FACTOR, easeFactor - 0.2);
                newIntervalDays = 1;
                break;
        }

        if (examDate != null) {
            long daysUntilExam = ChronoUnit.DAYS.between(now.toLocalDate(), examDate);
            if (daysUntilExam > 0) {
                long maxAllowedInterval = Math.max(1, daysUntilExam / 2);
                newIntervalDays = (int) Math.min(newIntervalDays, maxAllowedInterval);
            }
        }

        indexCard.setRepetitions(newRepetitions);
        indexCard.setEaseFactor(newEaseFactor);
        indexCard.setIntervalDays(newIntervalDays);
        indexCard.setDueDate(now.plusDays(newIntervalDays));
    }

    /**
     * Retroactively pulls a card's due date forward when a project's exam date changes and the
     * card's current due date would otherwise fall later than the compressed review window
     * (mirrors the cap applied in {@link #schedule}). Does not touch ease factor, repetitions or
     * interval, so the card's SM-2 growth trajectory is unaffected for its next real assessment.
     */
    public static void applyExamDateCap(IndexCard indexCard, LocalDateTime now, LocalDate examDate) {
        if (examDate == null) {
            return;
        }

        long daysUntilExam = ChronoUnit.DAYS.between(now.toLocalDate(), examDate);
        if (daysUntilExam <= 0) {
            return;
        }

        long maxAllowedInterval = Math.max(1, daysUntilExam / 2);
        LocalDateTime latestAllowedDueDate = now.plusDays(maxAllowedInterval);

        if (indexCard.getDueDate().isAfter(latestAllowedDueDate)) {
            indexCard.setDueDate(latestAllowedDueDate);
        }
    }

    private static int nextInterval(
            int newRepetitions, int previousIntervalDays, double easeFactor,
            int firstIntervalDays, int secondIntervalDays, double growthDampening) {
        int intervalDays;
        if (newRepetitions == 1) {
            intervalDays = firstIntervalDays;
        } else if (newRepetitions == 2) {
            intervalDays = secondIntervalDays;
        } else {
            intervalDays = (int) Math.round(previousIntervalDays * easeFactor * growthDampening);
        }
        return Math.max(1, intervalDays);
    }
}
