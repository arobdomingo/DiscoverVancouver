# Discover Vancouver

**In development** — a location-aware recommendation backend for discovering places around Vancouver.

## Implemented
- Spring Boot API controllers for places, recommendations, weather and health checks.
- MongoDB-backed place model, repository and seed-data support.
- Recommendation parameters for location, category, indoor preference, walking time, budget and atmosphere.
- Distance and recommendation service tests.

## Stack
Java, Spring Boot, Maven, MongoDB, Docker Compose.

## Structure
`src/main/java/com/discovervancouver/` contains `controller/`, `service/`, `model/`, `repository/`, `dto/` and `config/`. Tests are in `src/test/`.

## Run locally
Start MongoDB at `mongodb://localhost:27017/discover_vancouver` (the default configuration), then run:

```bash
mvn spring-boot:run
mvn test
```

Example request:

```http
GET http://localhost:8080/api/recommendations?lat=49.2827&lng=-123.1207&maxWalkingMinutes=20
```

## Roadmap
Expand place data, improve contextual recommendations, build the planned Android client, and strengthen integration tests.

**Current scope:** backend foundation; no completed Android application is included.
