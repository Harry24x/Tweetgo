# TweetGo

A full-stack microblogging platform (Twitter-like) with a React frontend and a Spring Boot REST API.
Built as a solo project for the Web Technology Project course at OTH Regensburg.

## Tech stack

| Layer | Technologies |
|---|---|
| Frontend | React 19, JavaScript, React Router, Bootstrap / React-Bootstrap, Axios, Vite |
| Backend | Java 21, Spring Boot 3.5, Spring Security, JWT, Spring Data JPA, Bean Validation |
| Database | MariaDB (H2 in-memory for tests) |
| API docs | Swagger / OpenAPI (springdoc) |
| Testing | JUnit 5, Mockito, MockMvc |
| Deployment | Docker, Docker Compose, Nginx |

## Features

- Registration and login with JWT authentication
- Create, view and delete posts
- Like / unlike posts and comment on posts
- Follow / unfollow users, user search and profile editing
- Hashtags with a hashtag feed
- Paginated timelines (own feed and user profiles)

## Architecture

```
React (Vite, served by Nginx) ──HTTP/JSON──▶ Spring Boot REST API ──JPA──▶ MariaDB
                                              │
                                              └─ JWT filter (Spring Security)
```

The backend exposes 18 REST endpoints under `/api/auth`, `/api/users` and `/api/tweet`.
Interactive API documentation is available in Swagger UI while the backend is running.

## Run with Docker (recommended)

Requirements: Docker and Docker Compose.

```bash
git clone https://github.com/Harry24x/Tweetgo.git
cd Tweetgo
cp .env.example .env        # then set DB_PASSWORD and JWT_SECRET (min. 32 characters)
docker compose up -d --build
```

| Service | URL |
|---|---|
| Frontend | http://localhost |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Adminer (DB admin) | http://localhost:8081 |

## Run locally for development

Backend (needs a running MariaDB, for example `docker compose up -d db`):

```bash
cd tweetgo-backend
export SPRING_DATASOURCE_PASSWORD=<your DB_PASSWORD>
export JWT_SECRET=<your JWT_SECRET>
./mvnw spring-boot:run
```

Frontend:

```bash
cd tweetgo-frontend
npm install
npm run dev                 # http://localhost:5173
```

## Tests

85 unit and integration tests cover controllers (MockMvc), services, entities, DTO validation and JWT security.
Tests use an in-memory H2 database, so no MariaDB is needed.

```bash
cd tweetgo-backend
./mvnw test
```

## Project structure

```
tweetgo-backend/    Spring Boot API (controllers, services, repositories, security, DTOs)
tweetgo-frontend/   React app (pages, components, auth context)
docker-compose.yml  MariaDB, Adminer, backend and frontend services
```
