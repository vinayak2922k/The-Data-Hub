# The Data Hub 🚀

> **High-Performance In-Memory RESTful API Server** built with **Java 17+**, **Spring Boot 3**, and **Maven**.

![The Data Hub Hero Banner](https://images.unsplash.com/photo-1558494949-ef010cbdcc31?auto=format&fit=crop&w=1200&q=80)

---

## 🌟 Overview

**The Data Hub** is a clean, production-grade RESTful API server engineered without reliance on an external relational or document database. It implements a fully thread-safe in-memory data store utilizing synchronized memory blocks and Java's `AtomicLong` for deterministic, zero-collision ID generation.

The server comes bundled with a **glassmorphism web UI & API Explorer** served directly from the root path (`/`), reactive ANSI middleware request logging, mock JWT authentication, standardized `{ success, message, data }` JSON response wrappers, and comprehensive unit/integration test coverage.

---

## 📸 Interactive Web Interface

Access the live server landing page at `http://localhost:5000/`:

```
┌────────────────────────────────────────────────────────────────────────┐
│  [★ The Data Hub]                     [● SERVER ONLINE - PORT 5000]   │
│                                                                        │
│               The Data Hub - High-Performance REST API                 │
│                                                                        │
│  [Total Posts: 0]   [Uptime: 00h 14m 22s]   [Requests Served: 42]     │
│                                                                        │
│  Interactive API Explorer:                                             │
│  [GET]    /posts       -> Retrieve all blog posts                      │
│  [POST]   /posts       -> Create post (@Valid title, content, author)  │
│  [GET]    /posts/{id}  -> Retrieve post by ID (404 if missing)         │
│  [PUT]    /posts/{id}  -> Update post by ID (404 if missing)         │
│  [DELETE] /posts/{id}  -> Remove post using removeIf stream filter     │
│  [POST]   /login       -> Mock JWT authentication (3-part Base64URL)   │
│  [GET]    /health      -> Live telemetry, uptime & request counters    │
└────────────────────────────────────────────────────────────────────────┘
```

---

## ✨ Key Features

- **Spring Boot 3 & Java 17+**: Modern Java architecture leveraging record-like DTOs, switch expressions, and Jakarta EE 10 annotations.
- **Thread-Safe In-Memory Engine**: Implemented via `private List<Post> blogPosts = new ArrayList<>()` with synchronized access guards and an `AtomicLong` ID sequencer.
- **Consistent Response Envelope**: All endpoints return the standardized JSON contract:
  ```json
  {
    "success": true,
    "message": "Operation description",
    "data": { ... }
  }
  ```
- **Global Exception Handling**: `@RestControllerAdvice` translates:
  - `PostNotFoundException` $\rightarrow$ `404 Not Found`
  - `MethodArgumentNotValidException` $\rightarrow$ `400 Bad Request` with field-level constraint errors
  - `NoResourceFoundException` / unmapped routes $\rightarrow$ `404 Not Found`
  - Unhandled errors $\rightarrow$ `500 Internal Server Error`
- **Colored ANSI Middleware Logger**: `OncePerRequestFilter` runs on every HTTP request and logs colored terminal output:
  - `GET` $\rightarrow$ **Green**
  - `POST` $\rightarrow$ **Blue**
  - `PUT` $\rightarrow$ **Yellow**
  - `DELETE` $\rightarrow$ **Red**
  - Format: `[GET] /posts - 10:05 AM`
- **Mock JWT Authentication**: `POST /login` validates email/password and returns a realistic 3-part `header.payload.signature` token encoded with Base64URL.
- **Zero-Dependency Startup**: Starts with an empty in-memory list by default. Setting `app.seed=true` (or `APP_SEED=true`) initializes 3 pre-configured demo posts.
- **Full CORS Enablement**: Allows cross-origin requests from any client origin (`*`).
- **Postman Collection**: Fully exported collection located in `/postman/the_data_hub_collection.json`.

---

## 📁 Package Architecture

The project adheres to a clean, decoupled MVC & service layer structure:

```
src/
├── main/
│   ├── java/com/datahub/
│   │   ├── DataHubApplication.java       # Application entry point & ASCII banner runner
│   │   ├── controller/
│   │   │   ├── AuthController.java       # POST /login (Mock JWT generator)
│   │   │   ├── CustomErrorController.java# Fallback JSON 404 for unknown routes
│   │   │   ├── HealthController.java     # GET /health telemetry endpoint
│   │   │   └── PostController.java       # CRUD REST controller (/posts)
│   │   ├── dto/
│   │   │   ├── ApiResponse.java          # Unified response envelope { success, message, data }
│   │   │   ├── HealthResponse.java       # Health telemetry payload
│   │   │   ├── LoginRequest.java         # Login credentials DTO (@NotBlank email, password)
│   │   │   ├── LoginResponse.java        # JWT token response DTO
│   │   │   └── PostRequest.java          # Post creation/update DTO with bean validation
│   │   ├── middleware/
│   │   │   ├── CorsConfig.java           # Cross-Origin Resource Sharing configuration
│   │   │   ├── GlobalExceptionHandler.java # @RestControllerAdvice centralized error handler
│   │   │   ├── PostNotFoundException.java# Domain exception for missing resources
│   │   │   └── RequestLoggingFilter.java # OncePerRequestFilter with ANSI colored console output
│   │   ├── model/
│   │   │   └── Post.java                 # In-memory Post domain entity
│   │   └── service/
│   │       └── PostService.java          # Thread-safe in-memory CRUD operations
│   └── resources/
│       ├── application.properties        # Port and seed configuration
│       └── static/
│           └── index.html                # Dark glassmorphism landing page & API Explorer
└── test/
    └── java/com/datahub/
        └── DataHubApplicationTests.java  # MockMvc integration tests
```

---

## 🛠️ Quickstart & Local Setup

### Prerequisites
- **JDK 17 or higher** installed (`java -version`)
- Maven 3.8+ (or use the bundled Maven Wrapper `./mvnw` / `mvnw.cmd`)

### 1. Clone & Enter Directory
```bash
git clone https://github.com/your-username/data-hub.git
cd data-hub
```

### 2. Run with Maven
Using Maven:
```bash
mvn spring-boot:run
```

Using the bundled Maven Wrapper:
- **macOS / Linux:**
  ```bash
  ./mvnw spring-boot:run
  ```
- **Windows (PowerShell):**
  ```powershell
  .\mvnw.cmd spring-boot:run
  ```

### 3. Build & Run Executable JAR
```bash
# Package the fat JAR
./mvnw clean package

# Run the standalone JAR (runs on port 5000 by default)
java -jar target/data-hub-0.0.1-SNAPSHOT.jar
```

To run on a custom port or enable sample seed posts:
```bash
# Custom Port and Sample Seed Posts
PORT=8080 APP_SEED=true java -jar target/data-hub-0.0.1-SNAPSHOT.jar
```

---

## 📡 API Reference & Endpoints

| Method | Endpoint | Description | Status Codes |
|---|---|---|---|
| `GET` | `/` | Serves the interactive Landing Page & API Explorer | `200 OK` |
| `GET` | `/health` | Server status, uptime duration, UTC timestamp, total posts | `200 OK` |
| `POST` | `/login` | Mock authentication generating 3-part Base64URL JWT | `200 OK`, `400 Bad Request` |
| `GET` | `/posts` | Retrieves all blog posts in memory | `200 OK` |
| `POST` | `/posts` | Creates a new post (`@Valid` title, content, author) | `201 Created`, `400 Bad Request` |
| `GET` | `/posts/{id}` | Retrieves a single post by ID | `200 OK`, `404 Not Found` |
| `PUT` | `/posts/{id}` | Updates existing post fields by ID | `200 OK`, `400 Bad Request`, `404 Not Found` |
| `DELETE` | `/posts/{id}` | Removes post using `removeIf` stream filter | `200 OK`, `404 Not Found` |
| `ANY` | `/unknown` | Unmapped path fallback returning standard JSON 404 | `404 Not Found` |

---

## 📝 Sample Requests & Responses

### 1. Create Post
**Request:**
`POST /posts`
```json
{
  "title": "Scaling REST APIs with Spring Boot 3",
  "content": "Designing stateless services with clean separation of concerns and robust error handling.",
  "author": "Demo Engineer"
}
```

**Response (`201 Created`):**
```json
{
  "success": true,
  "message": "Post created successfully",
  "data": {
    "id": 1,
    "title": "Scaling REST APIs with Spring Boot 3",
    "content": "Designing stateless services with clean separation of concerns and robust error handling.",
    "author": "Demo Engineer",
    "createdAt": "2026-11-14 14:48:42",
    "updatedAt": "2026-11-14 14:48:42"
  }
}
```

### 2. Validation Failure
**Request:**
`POST /posts`
```json
{
  "title": "",
  "content": "Tiny",
  "author": ""
}
```

**Response (`400 Bad Request`):**
```json
{
  "success": false,
  "message": "Validation failed: Title is required and cannot be blank",
  "data": {
    "title": "Title is required and cannot be blank",
    "content": "Content must be at least 5 characters long",
    "author": "Author is required and cannot be blank"
  }
}
```

### 3. Post Not Found
**Request:**
`GET /posts/999`

**Response (`404 Not Found`):**
```json
{
  "success": false,
  "message": "Post not found with ID: 999",
  "data": null
}
```

### 4. Mock JWT Authentication
**Request:**
`POST /login`
```json
{
  "email": "architect@datahub.io",
  "password": "Password123!"
}
```

**Response (`200 OK`):**
```json
{
  "success": true,
  "message": "Authentication successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhcmNoaXRlY3RAZGF0YWh1Yi5pbyIsImVtYWlsIjoiYXJjaGl0ZWN0QGRhdGFodWIuaW8iLCJpc3MiOiJUaGUgRGF0YSBIdWIiLCJpYXQiOjE3OTQ2Njg0MDAsImV4cCI6MTc5NDc1NDgwMCwicm9sZSI6IkRFVkVMT1BFUiJ9.bW9jay1zaWduYXR1cmUtZXhhbXBsZQ",
    "tokenType": "Bearer"
  }
}
```

### 5. Health Telemetry
**Request:**
`GET /health`

**Response (`200 OK`):**
```json
{
  "success": true,
  "message": "Server is healthy and operational",
  "data": {
    "status": "UP",
    "uptime": "01h 24m 10s",
    "timestamp": "2026-11-14T09:18:42.881Z",
    "totalPosts": 3,
    "requestsServed": 142
  }
}
```

---

## 🐳 Docker Deployment

The project features a **multi-stage `Dockerfile`** minimizing the final image size and running under a secure non-root user.

### 1. Build the Docker Image
```bash
docker build -t data-hub:latest .
```

### 2. Run the Container
```bash
docker run -p 5000:5000 -e PORT=5000 -e APP_SEED=false data-hub:latest
```

Open `http://localhost:5000` in your browser.

---

## ☁️ Step-by-Step Deployment to Render.com

Render supports native Docker deployments directly from your Git repository:

1. **Push your code to GitHub / GitLab:**
   ```bash
   git init
   git add .
   git commit -m "feat: complete The Data Hub REST API server"
   git remote add origin https://github.com/<your-username>/data-hub.git
   git push -u origin main
   ```

2. **Create a New Web Service on Render:**
   - Log into [Render Dashboard](https://dashboard.render.com/).
   - Click **New +** $\rightarrow$ **Web Service**.
   - Connect your GitHub repository `data-hub`.

3. **Configure the Service:**
   - **Name:** `the-data-hub`
   - **Environment:** `Docker` (Render automatically detects the root `Dockerfile`).
   - **Region:** Select the region closest to you (e.g., Oregon, Frankfurt, Singapore).
   - **Instance Type:** `Free`.

4. **Environment Variables:**
   - Add the following environment variables under **Advanced**:
     - `PORT` = `5000` (or leave default, Render maps the application port automatically via `$PORT`).
     - `APP_SEED` = `false` (or `true` if you want initial sample posts).

5. **Deploy:**
   - Click **Create Web Service**.
   - Render will build the multi-stage Docker container and deploy the app.
   - Once the deployment shows **Live**, click your Render URL (e.g., `https://the-data-hub.onrender.com`) to open the interactive landing page!

---

## 🧪 Running Automated Tests

Run the full test suite using Maven:

```bash
# Run tests
./mvnw test

# Windows PowerShell
.\mvnw.cmd test
```

Test coverage includes:
- Spring Boot Application Context Load verification
- Health Check telemetry endpoint assertions
- Full CRUD execution flow (Create $\rightarrow$ Read $\rightarrow$ Validate $\rightarrow$ Delete)
- `@Valid` constraint validation verification
- Mock JWT authentication contract verification

---

## 👨‍💻 Author & Developer

**Vinayak Sharma**  
*Senior Java Backend Engineer &bull; Full-Stack Microservices*

- **GitHub:** [@vinayak2922k](https://github.com/vinayak2922k)
- **LinkedIn:** [Vinayak Sharma](https://linkedin.com/in/vinayak2922k)
- **Email:** [vinayaksharma2289@gmail.com](mailto:vinayaksharma2289@gmail.com)

---

## 📜 License

This project is licensed under the MIT License. Developed by Vinayak Sharma.

