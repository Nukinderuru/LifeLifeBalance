# Inner Council Backend

Ktor backend for a single-user self-care tracker built around fixed inner characters, wishes, and daily completions.

## Stack

- Kotlin
- Java 21
- Ktor
- PostgreSQL
- Exposed ORM
- Liquibase
- Koin
- kotlinx.serialization
- Docker Compose

## Features

- Fixed seeded characters on startup: `MAYA`, `ELINA`, `TORA`, `DANA`, `NAOMI`
- Wish CRUD with soft delete via `active=false`
- Completion tracking with one completion per wish per day
- Character daily and weekly scoring
- Configurable status thresholds
- Today and weekly dashboard endpoints
- OpenAPI spec and Swagger UI
- PostgreSQL-backed integration tests

## Requirements

- Java 21
- Docker and Docker Compose
- Gradle 8+ installed locally

## Local PostgreSQL

Start PostgreSQL:

```bash
docker compose up -d
```

Default connection settings:

- Database: `inner_council`
- Username: `inner_council`
- Password: `inner_council`
- JDBC URL: `jdbc:postgresql://localhost:5432/inner_council`

## Run the Application

With default config:

```bash
gradle run
```

With environment overrides:

```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/inner_council \
DATABASE_USERNAME=inner_council \
DATABASE_PASSWORD=inner_council \
gradle run
```

The app will:

- run Liquibase migrations on startup;
- seed the five predefined characters if they do not exist.

## API Docs

- OpenAPI spec: `http://localhost:8080/openapi`
- Swagger UI: `http://localhost:8080/swagger`

## Main Endpoints

- `GET /api/characters`
- `GET /api/characters/{id}`
- `GET /api/characters/{id}/summary?date=YYYY-MM-DD`
- `GET /api/wishes?characterId=...&category=DAILY&active=true`
- `GET /api/wishes/{id}`
- `POST /api/wishes`
- `PUT /api/wishes/{id}`
- `DELETE /api/wishes/{id}`
- `GET /api/completions?date=YYYY-MM-DD`
- `POST /api/completions`
- `DELETE /api/completions/{id}`
- `GET /api/dashboard/today`
- `GET /api/dashboard/week?endDate=YYYY-MM-DD`

## Weekly Dashboard Contract

`GET /api/dashboard/week` always returns a 7-day window.

- `endDate` defaults to today when omitted.
- `startDate = endDate - 6 days`.
- `days` always contains exactly 7 entries.
- Each day contains all predefined characters.
- Daily character status is computed from that day’s score.
- Weekly character status is computed from the 7-day total score.

## Configuration

Application configuration lives in `src/main/resources/application.yaml`.

Status thresholds are configurable:

```yaml
app:
  statusThresholds:
    starvingMax: 9
    hungryMax: 29
    contentMax: 59
    happyMax: 99
```

## Tests

Integration tests use Testcontainers PostgreSQL.

Run tests:

```bash
gradle test
```

## Notes

- Characters are fixed and are not editable through the API.
- Wish delete is a soft delete.
- Duplicate completion for the same wish and date returns `409 Conflict`.
- Character summary status uses the weekly 7-day score ending at the requested date.
