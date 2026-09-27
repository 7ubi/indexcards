# Projects

A project groups index cards that belong together, for example all cards for one subject or one exam.

## Overview

After logging in, the start page lists all of your projects together with the number of cards they contain. Click on
a project to open it.

## Create a project

Click the button to create a new project and enter:

| Field     | Required | Notes                                                               |
|-----------|----------|---------------------------------------------------------------------|
| Name      | yes      | At most 100 characters. Must be unique among **your** projects.     |
| Exam date | no       | Used by spaced repetition to schedule all reviews before the exam.  |

## Project page

The project page shows the exam date (if set) and all index cards of the project, each with its current rating and
the date of its next review. From here you can:

- create a new index card,
- edit or delete existing index cards,
- start [spaced repetition](studying.md#spaced-repetition) or [practice](studying.md#practice),
- open the [statistics](studying.md#statistics),
- [import or export](import-export.md) cards as CSV.

## Edit a project

Name and exam date can be changed on the project's edit page. The same rules as for creating apply.

When the exam date is set or moved closer, cards whose next review would fall too late are pulled forward so they are
still reviewed in time (see [Exam date](studying.md#exam-date)). The rest of a card's learning progress is not
changed.

## Delete a project

Deleting a project also deletes all of its index cards and their rating history. You have to confirm the deletion in
a dialog.
