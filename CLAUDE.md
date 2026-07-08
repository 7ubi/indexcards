# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Structure

This repo has three parts:

- `indexcards/` — Backend (Spring Boot 2.7, Java 11)
- `indexcards-ui/` — Frontend (Angular 22, standalone components, Angular Material)
- `indexcards-docu/` — Documentation site (MkDocs), published to https://documentation.indexcards.7ubi.de/

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

`.github/workflows/pipeline.yml`: on push/PR to `main` — builds+tests backend (Maven), then lints+format-checks frontend (Eslint/Prettier). On push to `main` only, deploys via `docker-compose` on a self-hosted runner, then redeploys the docs site.

## Backend architecture

Package root: `com.x7ubi.indexcards`.

- **Controllers** (`controller/`) are thin: extract `username` from the `Authorization` header via `JwtUtils.getUsernameFromAuthorizationHeader`, then delegate to a service. Auth uses stateless JWT (`jwt/JwtUtils.java`, `jwt/AuthEntryPointJwt.java`, `config/AuthTokenFilter.java`); `SecurityConfig` permits `/api/auth/**` and `/api/test/**` and requires auth on everything else.
- **Services are split one-per-operation**, not one service per entity. E.g. index cards have `CreateIndexCardService`, `EditIndexCardService`, `DeleteIndexCardService`, `IndexCardAssessmentService`, `IndexCardQuizService`, plus a read-only `IndexCardService`. Each of these extends `AbstractIndexCardService` (and `AbstractProjectService` for projects), which holds shared repos and common guard methods: `getUser(username)`, `getProjectNotFoundError(id)`, `getIndexCardNotFoundError(id)`, `getProjectOwnerError(user, project)`. When adding a new index-card or project operation, add a new service extending the relevant `Abstract*Service` rather than growing an existing service class.
- **Ownership check pattern**: nearly every operation loads the `User` by username, loads the target entity, then calls the abstract class's `*NotFoundError` and `getProjectOwnerError` guards before mutating — throwing `EntityNotFoundException` / `UnauthorizedException` (handled centrally by `exceptions/GlobalExceptionHandler.java`).
- **Question/answer are stored as `byte[]`** (`@Lob`) on `IndexCard`, not `String` — mapping to/from `String` happens in `mapper/IndexCardMapper.java` (MapStruct). Keep this in mind when adding fields that touch these values.
- `IndexCard.assessment` is persisted `@Enumerated(EnumType.ORDINAL)` — do not reorder the `Assessment` enum values, it will corrupt existing data.
- Request/response DTOs live under `request/<domain>/` and `response/<domain>/`, mirrored by mappers (`ProjectMapper`, `IndexCardMapper`) rather than exposing entities directly from controllers.

## Frontend architecture

- Angular 22 standalone components (no NgModules) with the `component`/`pages`/`service`/`directives` split under `src/app/`.
- Routing (`app.routes.ts`) nests project sub-routes under `project/:id/...` and guards top-level and project routes with `LoginRequired` (`service/login/login-required.ts`).
- All backend calls go through `service/http/http.service.ts`, which wraps `HttpClient` methods (`get`/`post`/`put`/`delete`) with a shared callback-style `subscribe` helper: on any `401` (except `user_not_project_owner`) it force-logs-out via `LoginService`, and always surfaces errors through `SnackbarService`. New API calls should go through this service rather than injecting `HttpClient` directly, to keep this error/logout behavior consistent.
- Auth token/bearer handling is in `service/login/login.service.ts`, backed by `service/local/local.service.ts` (localStorage wrapper). `LoginService.getHeaderWithBearer()` is used by `HttpService` for every request.
- Response DTO shapes are declared in `app.responses.ts` (`LoginResponse`, `IndexCardResponse`, `ProjectResponse`, `Assessment` enum) — keep these in sync with the backend `response/*` classes and the `Assessment` enum ordinal order.
- ESLint enforces component selector prefix `app` (kebab-case) and directive prefix `app` (camelCase). `prefer-on-push-component-change-detection` is intentionally off — components were migrated to Angular 22 with `ChangeDetectionStrategy.Eager` preserved, and switching to real `OnPush` is a deliberate separate refactor, not something to do incidentally.
- Math rendering uses a custom `directives/mathjax.directive.ts`.
