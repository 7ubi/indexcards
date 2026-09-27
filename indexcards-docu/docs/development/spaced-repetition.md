# Spaced Repetition

Scheduling is implemented in `SpacedRepetitionScheduler` in the backend. It is inspired by the
[SM-2 algorithm](https://en.wikipedia.org/wiki/SuperMemo#Description_of_SM-2_algorithm), adapted to the app's three
ratings *Bad*, *Ok* and *Good* instead of SM-2's quality score from 0 to 5.

## State of a card

| Field          | Default | Meaning                                                         |
|----------------|---------|-----------------------------------------------------------------|
| `easeFactor`   | 2.5     | How fast the interval grows. Never lower than 1.3.              |
| `repetitions`  | 0       | Number of successful reviews in a row.                          |
| `intervalDays` | 0       | Days between the last review and the next one.                  |
| `dueDate`      | now     | When the card should be reviewed next.                          |

New cards have all these fields set to `NULL` in the database and use the defaults, so they are due immediately.

## Rating a card

When a card is rated via `POST /api/indexCard/assess`, `IndexCardAssessmentService` stores the rating in the card's
history and calls `SpacedRepetitionScheduler.schedule`:

| Rating | Repetitions | Ease factor           | Interval                                                     |
|--------|-------------|-----------------------|--------------------------------------------------------------|
| Bad    | reset to 0  | − 0.2 (min. 1.3)      | 1 day                                                        |
| Ok     | + 1         | − 0.05 (min. 1.3)     | 1st: 1 day, 2nd: 3 days, then `round(previous × EF × 0.8)`   |
| Good   | + 1         | + 0.1                 | 1st: 1 day, 2nd: 6 days, then `round(previous × EF)`         |

The interval is always at least 1 day. The new due date is `now + interval`.

*Ok* grows the interval more slowly than *Good* (dampening factor 0.8) and slightly lowers the ease factor, so cards
that are only known "okay" come back more often.

### Example

A new card rated *Good* every time (ease factor after each rating in brackets):

| Review | Ease factor | Interval |
|--------|-------------|----------|
| 1      | 2.6         | 1 day    |
| 2      | 2.7         | 6 days   |
| 3      | 2.8         | 17 days  |
| 4      | 2.9         | 49 days  |

## Exam date

If the card's project has an exam date in the future, the interval is capped:

```
maxInterval = max(1, daysUntilExam / 2)
interval    = min(interval, maxInterval)
```

This makes sure every card is reviewed several times before the exam, with the reviews getting closer together as the
exam approaches. Exam dates today or in the past are ignored.

When a project's exam date is changed (`PUT /api/project`), `applyExamDateCap` pulls the due date of every card forward
to `now + maxInterval` if it currently lies later. Ease factor, repetitions and interval are not changed, so the next
real rating continues from the card's previous progress.

## Selecting cards

`IndexCardQuizService` sorts all cards of a project by due date (ties by id) and returns the first **15**. This means
overdue cards come first, most overdue first. If fewer than 15 cards are due, the round is filled up with the cards that
are due next, so a round is never empty as long as the project has cards.

Practice mode does not use this endpoint; it loads all cards of the project and does not send ratings to the backend.
