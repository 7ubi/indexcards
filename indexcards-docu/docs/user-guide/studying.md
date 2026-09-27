# Studying

There are two ways to study the cards of a project: **spaced repetition** and **practice**. Both need at least one
index card in the project.

## Spaced repetition

Spaced repetition shows you the cards that are due for review. A round contains up to **15 cards**: the cards that are
most overdue come first. If fewer than 15 cards are due, the round is filled up with the cards whose review comes up
next.

For each card:

1. read the question and try to remember the answer,
2. flip the card to check the answer,
3. rate how well you knew it: **Bad**, **Ok** or **Good**.

The rating is saved immediately and decides when you see the card again:

| Rating   | Effect                                                                               |
|----------|--------------------------------------------------------------------------------------|
| **Bad**  | The card starts over and is due again tomorrow.                                      |
| **Ok**   | The card is due in 1 day, then 3 days, then the interval grows moderately.           |
| **Good** | The card is due in 1 day, then 6 days, then the interval grows faster over time.     |

The better you know a card, the longer the gaps between its reviews get. The details of the algorithm are described in
[Spaced Repetition](../development/spaced-repetition.md).

After the last card you are taken to the [statistics](#statistics).

### Exam date

If the project has an exam date, no review is scheduled further away than **half the remaining time until the exam**.
For example, with the exam in 10 days a card is due again in at most 5 days. This makes sure every card is repeated a
few times before the exam. Once the exam date has passed it is ignored.

## Practice

Practice goes through **all** cards of the project, independent of when they are due. You rate the cards like in
spaced repetition, but the ratings are **not saved** and do not change the review schedule. This is useful for a final
run through all cards, e.g. right before an exam.

At the end a chart shows how many cards you rated *Bad*, *Ok* and *Good* in this run. You can practice again or switch
to spaced repetition.

## Statistics

The statistics page shows a chart of how the cards of a project are currently rated (*Unrated*, *Bad*, *Ok*, *Good*),
based on the latest spaced repetition rating of each card. New cards are *Unrated*.
