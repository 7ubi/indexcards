# Frontend

The frontend in `indexcards-ui/` is an Angular 22 single page application using standalone components (no NgModules)
and Angular Material.

## Structure

```
src/
├── app/
│   ├── component/   reusable components (header, card flip, card overview, charts, dialogs, ...)
│   ├── directives/  mathjax and paste-image directives
│   ├── pages/       routed pages (auth, project, indexcard, account, privacy)
│   ├── pipes/       markdownMath pipe
│   ├── service/     http, login, local storage, image and snackbar services
│   ├── util/        helpers (e.g. ISO date conversion)
│   ├── app.routes.ts
│   └── app.responses.ts
└── assets/i18n/     translations (en.json, de.json)
```

## Routes

| Path                                    | Page                          | Login required |
|-----------------------------------------|-------------------------------|----------------|
| `/login`                                | Login                         | no             |
| `/signup`                               | Signup                        | no             |
| `/privacy`                              | Privacy policy                | no             |
| `/`                                     | All projects                  | yes            |
| `/account/delete`                       | Delete account                | yes            |
| `/project/create`                       | Create project                | yes            |
| `/project/:id`                          | Project with its index cards  | yes            |
| `/project/:id/edit`                     | Edit project                  | yes            |
| `/project/:id/createIndexCard`          | Create index card             | yes            |
| `/project/:id/editIndexCard/:indexCardId` | Edit index card             | yes            |
| `/project/:id/quiz`                     | Spaced repetition             | yes            |
| `/project/:id/quiz/stat`                | Statistics                    | yes            |
| `/project/:id/practice`                 | Practice                      | yes            |
| `**`                                    | Page not found                | no             |

Protected routes use the `LoginRequired` guard (`service/login/login-required.ts`), which redirects to `/login` if
there is no token.

## Backend communication

All backend calls go through `HttpService` (`service/http/http.service.ts`). It wraps `HttpClient`'s `get`, `post`,
`put` and `delete` and

- adds the `Authorization: Bearer <token>` header from `LoginService`,
- shows errors with `SnackbarService`, translating the backend error code (`backend.<code>`),
- logs the user out on any `401` response, except `user_not_project_owner`.

New API calls should use `HttpService` instead of injecting `HttpClient` directly, to keep this behaviour consistent.

The token is stored in local storage via `LocalService` and managed by `LoginService`.

Response types are declared in `app.responses.ts`. Keep them in sync with the backend's `response` classes; the
`Assessment` enum must have the same order as in the backend.

## Rendering cards

Card content is Markdown with LaTeX math:

1. The `markdownMath` pipe replaces math regions (`$...$`, `$$...$$`, `\(...\)`, `\[...\]`) with placeholders, renders
   the Markdown with [marked](https://marked.js.org/) and puts the math back in.
2. The `appMathjax` directive typesets the math with MathJax 3, which is loaded from a CDN in `index.html`. It observes
   the element and re-typesets when the content changes (e.g. in the editor preview).

MathJax is configured in `index.html` with the `ui/safe` extension, which filters potentially dangerous TeX commands
from user content.

## Image upload

The `appPasteImage` directive can be added to a `textarea` bound to a form control. On paste it compresses the image
in the browser (`compressImage` in `image.service.ts`: max. 1600 px, JPEG quality 0.82), uploads it to
`POST /api/images` and inserts `![image](/api/images/<id>)` at the cursor.

## Translations

Texts are translated with [ngx-translate](https://github.com/ngx-translate/core). The files are
`src/assets/i18n/en.json` and `src/assets/i18n/de.json`; add every new key to both. The language is detected from the
browser on the first visit and stored in local storage under `language`.

## Code style

- ESLint (`npm run lint`) enforces the `app` prefix for component selectors (kebab-case) and directives (camelCase).
- Prettier (`npm run format:check`) with a print width of 100 and single quotes.
- Components use `ChangeDetectionStrategy.Eager` and trigger change detection explicitly with `ChangeDetectorRef`.

Lint and format are checked in the [pipeline](../pipeline.md#linting).
