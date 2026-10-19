# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Structure

This repo has three parts:

- `indexcards/` — Backend (Spring Boot 4.1, Java 21)
- `indexcards-ui/` — Frontend (Angular 22, standalone components, Angular Material)
- `indexcards-docu/` — Documentation site (MkDocs), published via GitHub Pages (`gh-pages` branch) to https://documentation.indexcards.7ubi.de/

The app is index cards for studying: users create Projects, each containing IndexCards (question/answer pairs) that get quizzed and rated (`UNRATED`/`BAD`/`OK`/`GOOD`) to drive spaced repetition.

## Commands

### Backend (run from `indexcards/`)

- Build + run all tests: `./mvnw clean package` (or `mvn -B package` as CI does)
- Run a single test class: `./mvnw test -Dtest=CreateIndexCardServiceTest`
- Run a single test method: `./mvnw test -Dtest=CreateIndexCardServiceTest#methodName`
- Tests use JUnit 5 + H2 in-memory DB (see `TestConfig.java`, `*TestConfig.java` per feature)

### Frontend (run from `indexcards-ui/`)

- Dev server: `npm start` (proxies `/api/**` to `http://localhost:8080`, see `src/proxy.conf.json`)
- Build: `npm run build`
- Unit tests (Vitest): `npm test`
- Lint: `npm run lint`
- Format check: `npm run format:check` / fix: `npm run format`

### Full stack locally

`docker-compose.yml` at repo root spins up MySQL + backend + frontend together (used for self-hosted deploy). For local dev, run backend and `ng serve` separately instead — the proxy config expects the backend on `localhost:8080`.

## CI Pipeline

`.github/workflows/pipeline.yml`: on push/PR to `main` — builds+tests backend (Maven), then lints+format-checks frontend (Eslint/Prettier). On push to `main` only, deploys via `docker-compose` on a self-hosted runner, and deploys the docs site to GitHub Pages with `mkdocs gh-deploy`.

## Backend architecture

Package root: `com.x7ubi.indexcards`.

- **Controllers** (`controller/`) are thin: they get the current user via `@AuthenticationPrincipal SecurityUser` (use `securityUser.getUsername()`), then delegate to a service. Auth is OIDC via Authentik (username/password accounts stored in Authentik, or Google): the backend is an OAuth2 resource server (`config/SecurityConfig.java`) validating Authentik's access tokens (JWKS, issuer and audience from `spring.security.oauth2.resourceserver.jwt.*`). `config/AuthentikJwtAuthenticationConverter.java` maps the token's `sub` to a local `User` (`oidc_subject` column) via `OidcUserProvisioningService`, creating the account on first use, and `AdminGroupService` grants `Role.ADMIN` to members of the Authentik group `indexcards-admins` (token claim `groups`; usernames are user-chosen, so never grant roles by username). Deleting an account (`DeleteUserService`) also deletes the Authentik user through the Authentik API (`AuthentikUserDeletionService`, service-account token `AUTHENTIK_API_TOKEN`; token `sub` is the Authentik user UUID); an Authentik failure rolls the deletion back. `SecurityConfig` permits `/api/auth/**` (only `GET /api/auth/config`) and `/api/test/**` and requires auth on everything else. Authentik itself runs in `docker-compose.yml` and is configured by `authentik/blueprints/indexcards.yaml`.
- **Services are split one-per-operation**, not one service per entity. E.g. index cards have `CreateIndexCardService`, `EditIndexCardService`, `DeleteIndexCardService`, `IndexCardAssessmentService`, `IndexCardQuizService`, plus a read-only `IndexCardService`. Each of these extends `AbstractIndexCardService` (and `AbstractProjectService` for projects), which holds shared repos and common guard methods: `getUser(username)`, `getProjectNotFoundError(id)`, `getIndexCardNotFoundError(id)`, `getProjectOwnerError(user, project)`. When adding a new index-card or project operation, add a new service extending the relevant `Abstract*Service` rather than growing an existing service class.
- **Ownership check pattern**: nearly every operation loads the `User` by username, loads the target entity, then calls the abstract class's `*NotFoundError` and `getProjectOwnerError` guards before mutating — throwing `EntityNotFoundException` / `UnauthorizedException` (handled centrally by `exceptions/GlobalExceptionHandler.java`).
- **Question/answer are stored as `byte[]`** (`@Lob`) on `IndexCard`, not `String` — mapping to/from `String` happens in `mapper/IndexCardMapper.java` (MapStruct). Keep this in mind when adding fields that touch these values.
- `IndexCard.assessment` is persisted `@Enumerated(EnumType.ORDINAL)` — do not reorder the `Assessment` enum values, it will corrupt existing data.
- Request/response DTOs live under `request/<domain>/` and `response/<domain>/`, mirrored by mappers (`ProjectMapper`, `IndexCardMapper`) rather than exposing entities directly from controllers.

## Frontend architecture

- Angular 22 standalone components (no NgModules) with the `component`/`pages`/`service`/`directives` split under `src/app/`.
- Routing (`app.routes.ts`) nests project sub-routes under `project/:id/...` and guards top-level and project routes with `LoginRequired` (`service/login/login-required.ts`).
- All backend calls go through `service/http/http.service.ts`, which wraps `HttpClient` methods (`get`/`post`/`put`/`delete`) with a shared callback-style `subscribe` helper: on any `401` (except `user_not_project_owner` and `username_confirmation_mismatch`) it logs out locally via `LoginService`, and always surfaces errors through `SnackbarService`. New API calls should go through this service rather than injecting `HttpClient` directly (only `LoginService` loads `/api/auth/config` with `HttpClient`, to avoid a circular dependency).
- Login is in `service/login/login.service.ts`: OIDC Authorization Code + PKCE against Authentik with `oidc-client-ts` (created through the `USER_MANAGER_FACTORY` token so tests can replace it; `vi.mock` does not work with the Angular test builder). `init()` runs as an app initializer; Authentik redirects back to `/login/callback` (`pages/auth/login-callback`). `logout()` ends the session locally, `signOut()` (header button) also ends the Authentik session. Tokens live in session storage.
- Response DTO shapes are declared in `app.responses.ts` (`LoginResponse`, `IndexCardResponse`, `ProjectResponse`, `Assessment` enum) — keep these in sync with the backend `response/*` classes and the `Assessment` enum ordinal order.
- ESLint enforces component selector prefix `app` (kebab-case) and directive prefix `app` (camelCase). `prefer-on-push-component-change-detection` is intentionally off — components were migrated to Angular 22 with `ChangeDetectionStrategy.Eager` preserved, and switching to real `OnPush` is a deliberate separate refactor, not something to do incidentally.
- Math rendering uses a custom `directives/mathjax.directive.ts`.
