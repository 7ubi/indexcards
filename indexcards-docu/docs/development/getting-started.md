# Getting Started

This page describes how to run Indexcards locally for development.

## Requirements

| Tool         | Version | Used for                         |
|--------------|---------|----------------------------------|
| Java (JDK)   | 11      | Backend                          |
| Maven        | 3.6+    | Backend build (or use `./mvnw`)  |
| Node.js      | 22+     | Frontend                         |
| MySQL        | 5.7     | Database (e.g. via Docker)       |
| Python       | 3.9+    | Documentation (optional)         |

## Database

The backend expects a MySQL database `indexcardsdb` on port `3306` (host from `MYSQL_HOST`, default `localhost`) with
user `root` and password `12345678`. The quickest way is a Docker container:

```bash
docker run -d --name indexcards-db -p 3306:3306 \
  -e MYSQL_DATABASE=indexcardsdb -e MYSQL_ROOT_PASSWORD=12345678 \
  mysql:5.7
```

The schema is created automatically by [Flyway](../database.md#migrations) on the first start of the backend.

## Backend

```bash
cd indexcards
./mvnw spring-boot:run
```

The backend starts on [http://localhost:8080](http://localhost:8080). Without `JWT_SECRET` set, a random signing key is
generated on every start, which means you have to log in again after restarting the backend. Uploaded images are stored
in `./data/images` (see [configuration](../operations/deployment.md#configuration)).

### Tests

The tests use JUnit 5 and an in-memory H2 database, so no MySQL is needed:

```bash
./mvnw clean package                                     # build and run all tests
./mvnw test -Dtest=CreateIndexCardServiceTest            # single test class
./mvnw test -Dtest=CreateIndexCardServiceTest#methodName # single test method
```

## Frontend

```bash
cd indexcards-ui
npm install
npm start
```

The app is available on [http://localhost:4200](http://localhost:4200). The dev server proxies all requests to
`/api/**` to the backend on `http://localhost:8080` (see `src/proxy.conf.json`).

| Command                | Description                         |
|------------------------|-------------------------------------|
| `npm start`            | Dev server with live reload         |
| `npm run build`        | Production build into `dist/`       |
| `npm test`             | Unit tests (Vitest)                 |
| `npm run lint`         | ESLint                              |
| `npm run format:check` | Check formatting with Prettier      |
| `npm run format`       | Fix formatting with Prettier        |

Lint and format are checked in the [pipeline](../pipeline.md), so run them before pushing.

## Documentation

```bash
cd indexcards-docu
pip install -r requirements.txt
mkdocs serve
```

The documentation is then available on [http://localhost:8000](http://localhost:8000).

## Full stack with Docker

The `docker-compose.yml` in the repository root starts MySQL, backend and frontend together. It is meant for the
[deployment](../operations/deployment.md) and requires an external Docker network; for development running the parts
separately as described above is simpler.

## Versioning

Backend and frontend share one version number. To change it, run

```bash
scripts/bump-version.sh 2.3.0
```

This updates the version in `indexcards/pom.xml`, the jar name in `indexcards/Dockerfile` and
`indexcards-ui/package.json`.
