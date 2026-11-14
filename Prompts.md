# Prompts Log - The Data Hub Development

This document logs all user prompts received during the development of **The Data Hub** RESTful API server, along with notes summarizing the engineering decisions and technical changes implemented.

---

## Prompt 1: Initial Architecture & Full-Stack Implementation
**Date / Timestamp:** 2026-11-14T14:20:52+05:30

### Prompt Text:
```text
You are a senior Java backend engineer. Build "The Data Hub", a RESTful API
server using Java 17+, Spring Boot 3 and Maven, in this workspace. Work phase
by phase and explain each step briefly so I can understand the code (I must
explain it in my demo video). Do not use a database: the in-memory list is a
requirement.

=== PROJECT SETUP ===
- Spring Initializr style project: group com.datahub, artifact data-hub.
- Dependencies: spring-boot-starter-web, lombok (optional), spring-boot-devtools.
- application.properties: server.port=${PORT:5000}
- Package structure: controller, model, service, middleware, dto.

=== PHASE 1 (P0): Server and route scaffolding ===
- Main class DataHubApplication, server running on port 5000.
- PostController with @RestController and @RequestMapping("/posts"):
  GET /posts, GET /posts/{id}, POST /posts, PUT /posts/{id}, DELETE /posts/{id}
- At first, each returns a static JSON like { "message": "Route active" }.

=== PHASE 2 (P1): In-memory database and CRUD ===
- Post model: id (Long), title, content, author, createdAt, updatedAt.
- PostService holding `private List<Post> blogPosts = new ArrayList<>();`
  (use an AtomicLong for id generation, and keep it thread-safe).
- POST /posts: read the payload with @RequestBody, validate title, content
  and author (@Valid, spring-boot-starter-validation), add to the list,
  return 201 with the created post.
- GET /posts: return the entire list.
- GET /posts/{id}: return one post, or 404 with a JSON error.
- PUT /posts/{id}: update an existing post, 404 if missing.
- DELETE /posts/{id}: remove using removeIf() / stream filter on the id,
  return a confirmation message.
- Consistent response wrapper: { success, message, data }.
- A @RestControllerAdvice global exception handler (PostNotFoundException ->
  404, validation errors -> 400, generic -> 500).
- Enable CORS for all origins.

=== PHASE 3 (P2): Middleware and auth mocking ===
- Custom logger as a OncePerRequestFilter (or HandlerInterceptor) that runs on
  EVERY request and logs to the console in this format:
  [GET] /posts - 10:05 AM
  Use ANSI colors: GET=green, POST=blue, PUT=yellow, DELETE=red.
- AuthController with POST /login: accepts { "email", "password" } (400 if
  missing) and returns { "token": "<mock JWT>" }. Build a realistic
  header.payload.signature string using Base64 URL encoding (no real
  signature validation needed, since this is mock auth).
- Add a handler for unknown routes returning a JSON 404.

=== UNIQUE / EYE-CATCHING EXTRAS ===
1. A stunning landing page at GET / served from src/main/resources/static/index.html:
   - Dark modern theme with glassmorphism or neon gradients, responsive.
   - Hero: "The Data Hub" with an animated gradient and a pulsing
     "Server Online - Port 5000" badge.
   - An "API Explorer": a card per endpoint with a colored method badge, path,
     description and a "Try it" button that calls the real API with fetch()
     and shows the pretty-printed JSON response.
   - Live stats strip: total posts, uptime, requests served.
   - Hover animations, Google font (Inter/Poppins), copy-to-clipboard buttons.
2. GET /health returning status, uptime and timestamp.
3. A colorful ASCII banner printed in the console on startup (CommandLineRunner)
   with the port and URL.
4. Optional seed data of 3 sample posts only when app.seed=true, so the
   default list stays empty as required.
5. A Postman collection in /postman covering all endpoints including /login.

=== DEPLOYMENT (Render.com or Railway.app) ===
- Add a multi-stage Dockerfile (Maven build stage, then a JRE 17 runtime
  stage) that exposes the PORT env variable.
- Make sure the jar runs with: java -jar target/data-hub-0.0.1-SNAPSHOT.jar
- Add a .gitignore (target/, .idea/, *.iml) and .env.example.
- Write a professional README.md: description, screenshot placeholder, setup
  (mvn spring-boot:run), endpoint table, sample requests/responses, and
  step-by-step Render deployment with Docker.
- Create Prompts.md and log every prompt I give you with a short note on
  what changed.
```

### Changes Implemented:
1. **Project Setup & Architecture:**
   - Established Maven project with Spring Boot 3.3.5 and Java 17+.
   - Created Maven wrapper (`mvnw`, `mvnw.cmd`, `.mvn/wrapper/`).
   - Defined strict package organization: `com.datahub.{controller, model, service, middleware, dto}`.
   - Configured `application.properties` with `${PORT:5000}` dynamic port resolution and optional `${APP_SEED:false}`.

2. **Phase 1 & 2 (In-Memory Database & Full CRUD):**
   - Created `Post` entity with `id`, `title`, `content`, `author`, `createdAt`, `updatedAt`.
   - Created `PostService` maintaining `private List<Post> blogPosts = new ArrayList<>();` with `AtomicLong` for thread-safe sequential ID generation and synchronized blocks.
   - Implemented conditional seed data initialization on `@PostConstruct` when `app.seed=true` (keeping list empty by default).
   - Created `ApiResponse<T>` unified response wrapper `{ success, message, data }`.
   - Built `PostController` covering `GET /posts`, `GET /posts/{id}`, `POST /posts` (201 Created), `PUT /posts/{id}`, `DELETE /posts/{id}` (via `removeIf`).
   - Created `GlobalExceptionHandler` with `@RestControllerAdvice` mapping `PostNotFoundException` -> 404, `MethodArgumentNotValidException` -> 400 with field details, and generic exceptions -> 500.
   - Configured CORS for all origins via `CorsConfig` implementing `WebMvcConfigurer`.

3. **Phase 3 (Middleware & Mock Auth):**
   - Implemented `RequestLoggingFilter` (`OncePerRequestFilter`) with ANSI color coding (GET=green, POST=blue, PUT=yellow, DELETE=red) logging `[METHOD] /path - hh:mm a` and incrementing a telemetry counter.
   - Implemented `AuthController` with `POST /login` validating email & password, producing a realistic 3-part Base64 URL-encoded mock JWT (`header.payload.signature`).
   - Added unknown route fallback via `CustomErrorController` mapping `/error` to structured JSON 404.

4. **Unique Extras:**
   - Designed and built a dark glassmorphism interactive landing page (`src/main/resources/static/index.html`) featuring animated gradient hero, pulsing server status badge, live auto-refreshing telemetry strip (posts, uptime, requests served), and an interactive API Explorer with "Try it" buttons, copy-to-clipboard, and formatted JSON output.
   - Implemented `HealthController` (`GET /health`) returning status, formatted uptime duration, UTC timestamp, total post count, and request counter.
   - Added custom ASCII startup banner runner via `CommandLineRunner` in `DataHubApplication`.
   - Created Postman collection in `postman/the_data_hub_collection.json` covering all endpoints and edge cases.

5. **Deployment & DevOps:**
   - Authored multi-stage `Dockerfile` (Maven build stage -> Eclipse Temurin JRE 17 runtime stage) with non-root security user, `HEALTHCHECK`, and dynamic `$PORT` exposure.
   - Added `.gitignore` and `.env.example`.
   - Packaged and verified executable JAR `target/data-hub-0.0.1-SNAPSHOT.jar`.
   - Authored comprehensive `README.md`.
