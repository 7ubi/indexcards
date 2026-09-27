# Indexcards

[![indexcard pipeline](https://github.com/7ubi/indexcards/actions/workflows/pipeline.yml/badge.svg)](https://github.com/7ubi/indexcards/actions/workflows/pipeline.yml)

Indexcards is a web application for studying with index cards, built with Angular and Spring Boot.

Users organise their cards in **projects** (e.g. one per subject or exam). Each index card is a question/answer pair
that is studied either with **spaced repetition**, where cards are rated and scheduled for their next review, or in a
free **practice** mode that goes through all cards without affecting the schedule.

## Features

- **Projects** with an optional **exam date**; reviews are compressed so all cards are repeated before the exam.
- **Index cards** with [Markdown](https://www.markdownguide.org/basic-syntax/) and LaTeX math (rendered with
  [MathJax](https://www.mathjax.org/)).
- **Images** in cards: paste an image from the clipboard directly into the question or answer.
- **Spaced repetition** based on the SM-2 algorithm with the ratings *Bad*, *Ok* and *Good*.
- **Practice mode** to go through all cards of a project without changing their schedule.
- **Statistics** showing how the cards of a project are currently rated.
- **CSV import and export** of index cards.
- **Account management** including permanent deletion of the account and all its data.
- Available in **English** and **German**.

## Where to go next

| If you want to...                          | Read                                                   |
|--------------------------------------------|--------------------------------------------------------|
| learn how to use the app                   | [User Guide](user-guide/account.md)                    |
| run the project locally                    | [Getting Started](development/getting-started.md)      |
| understand the code                        | [Project Structure](projectStructure.md)               |
| call the backend directly                  | [REST API](development/api.md)                         |
| deploy or configure your own instance      | [Deployment](operations/deployment.md)                 |
