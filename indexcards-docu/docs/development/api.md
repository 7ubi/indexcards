# REST API

All endpoints are under `/api`. Request and response bodies are JSON unless noted otherwise.

Except for `/api/auth/**` and `GET /api/images/{id}`, every request needs the header

```
Authorization: Bearer <token>
```

with a token from [login](#login). Errors are returned as a plain text error code, see
[Error handling](backend.md#error-handling).

## Authentication

### Signup

`POST /api/auth/signup` → `201 Created`

```json
{
  "username": "jane",
  "firstname": "Jane",
  "surname": "Doe",
  "password": "secret"
}
```

Errors: `400 username_exists`

### Login

`POST /api/auth/login` → `200 OK`

```json
{ "username": "jane", "password": "secret" }
```

Response:

```json
{ "token": "eyJhbGciOi...", "type": "Bearer", "id": 1, "username": "jane" }
```

Errors: `401 bad_credentials`

## User

### Get account

`GET /api/user` → `200 OK`

```json
{ "username": "jane", "firstname": "Jane", "surname": "Doe", "admin": false }
```

`admin` is `true` if the user has the `ADMIN` role; the frontend uses it to show the analytics page.

### Delete account

`DELETE /api/user` → `204 No Content`

Deletes the logged-in user with all projects, index cards, rating history and images.

```json
{ "password": "secret" }
```

Errors: `401 wrong_password`

## Projects

### Get all projects

`GET /api/project` → `200 OK`, list of [`ProjectResponse`](#projectresponse)

### Get project

`GET /api/project/{id}` → `200 OK`, [`ProjectResponse`](#projectresponse) including all index cards

### Create project

`POST /api/project` → `201 Created`

```json
{ "name": "Biology", "examDate": "2026-12-01" }
```

`examDate` is optional (`null`). Errors: `400 project_name_exists`, `400 project_name_too_long`

### Edit project

`PUT /api/project?id={id}` → `200 OK`

Same body as create. If an exam date is set, due dates of the project's cards are capped
(see [Spaced Repetition](spaced-repetition.md#exam-date)).

### Delete project

`DELETE /api/project?id={id}` → `204 No Content`

## Index cards

### Get index card

`GET /api/indexCard?id={id}` → `200 OK`, [`IndexCardResponse`](#indexcardresponse)

### Create index card

`POST /api/indexCard` → `201 Created`

```json
{ "projectId": 1, "question": "What is $2+2$?", "answer": "**4**" }
```

### Edit index card

`PUT /api/indexCard?id={id}` → `200 OK`

Same body as create (`projectId` is ignored). Rating and schedule are not changed.

### Delete index card

`DELETE /api/indexCard` → `204 No Content`

```json
{ "indexcardId": 5 }
```

### Get cards for spaced repetition

`GET /api/indexCard/quizIndexCards?id={projectId}` → `200 OK`, list of up to 15
[`IndexCardResponse`](#indexcardresponse), sorted by due date (most overdue first).

### Rate a card

`POST /api/indexCard/assess` → `200 OK`

```json
{ "indexCardId": 5, "assessment": "GOOD" }
```

`assessment` is one of `BAD`, `OK`, `GOOD`. Saves the rating in the card's history and schedules its next review.

### Import CSV

`POST /api/indexCard/import` → `201 Created`

```json
{ "projectId": 1, "csv": "\"Question 1\",\"Answer 1\"\n\"Question 2\",\"Answer 2\"" }
```

See [Import & Export](../user-guide/import-export.md#format) for the CSV format.

## Images

### Upload image

`POST /api/images` (`multipart/form-data`, field `file`) → `201 Created`

```json
{ "imageId": "3f2b8c1e-8f5d-4c7a-9b0e-2a6d1f4e7c90" }
```

The file must have an `image/*` content type and be at most 5 MB. Errors: `400 invalid_image`

### Get image

`GET /api/images/{imageId}` → `200 OK`, `image/jpeg`. No authentication required.

Errors: `400 image_not_found`

## Admin

Endpoints under `/api/admin/**` require the `ADMIN` role. Other users get `403 forbidden`.

### Get analytics

`GET /api/admin/analytics` → `200 OK`

```json
{
  "totals": { "users": 2, "activeProjects": 1, "archivedProjects": 0, "indexCards": 1, "assessments": 1 },
  "assessmentDistribution": { "unrated": 0, "bad": 0, "ok": 0, "good": 1 },
  "dailyActivity": [ { "date": "2026-09-29", "assessments": 1, "activeUsers": 1 } ],
  "dailySignups": [ { "date": "2026-09-29", "count": 2 } ],
  "users": [
    {
      "username": "bob",
      "createdAt": "2026-09-29T09:39:25",
      "projects": 1,
      "indexCards": 1,
      "assessments": 1,
      "lastActivity": "2026-09-29T09:39:26",
      "admin": false
    }
  ]
}
```

- `assessmentDistribution` counts index cards by their current rating.
- `dailyActivity` and `dailySignups` always contain the last 30 days (oldest first, today last); days without activity
  have `0`. `activeUsers` is the number of distinct users who rated at least one card that day.
- `users` is sorted by `lastActivity` (the latest rating), most recent first; users without ratings come last with
  `lastActivity: null`. `createdAt` is `null` for accounts created before signup dates were recorded. `admin` is
  `true` for users with the `ADMIN` role.

### Make a user admin

`PUT /api/admin/users/{username}/admin` → `204 No Content`

Gives the user the `ADMIN` role. Doing this for a user who is already an admin changes nothing. There is no endpoint
to remove the role.

Errors: `400 username_not_found`

## Common errors

| Status | Code                     | Meaning                                              |
|--------|--------------------------|------------------------------------------------------|
| 400    | `project_not_found`      | The project does not exist.                          |
| 400    | `indexcard_not_found`    | The index card does not exist.                       |
| 400    | `username_not_found`     | The user of the token does not exist anymore.        |
| 401    | `user_not_project_owner` | The project or card belongs to another user.         |
| 401    | —                        | Missing, invalid or expired token.                   |
| 403    | `forbidden`              | The endpoint requires the `ADMIN` role.              |

## Response types

### ProjectResponse

```json
{
  "id": 1,
  "name": "Biology",
  "examDate": "2026-12-01",
  "indexCardResponses": [ /* IndexCardResponse */ ]
}
```

### IndexCardResponse

```json
{
  "indexCardId": 5,
  "question": "What is $2+2$?",
  "answer": "**4**",
  "assessment": "UNRATED",
  "dueDate": "2026-09-28T10:15:30"
}
```

`assessment` is one of `UNRATED`, `BAD`, `OK`, `GOOD`.
