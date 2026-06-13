# Task Manager — Spring Boot Full-Stack App

A beginner-friendly full-stack web application built with Java Spring Boot + MySQL + plain HTML/CSS/JS.

## Tech Stack

| Layer    | Technology             |
|----------|------------------------|
| Frontend | HTML, CSS, JavaScript  |
| Backend  | Java 17 + Spring Boot 3 |
| Database | MySQL 8                |
| ORM      | Spring Data JPA        |
| Security | Spring Security (BCrypt)|
| Build    | Maven                  |

## Features

- User registration & login (passwords hashed with BCrypt)
- Create, read, update, delete tasks
- Mark tasks as complete / incomplete
- Filter tasks: All / Pending / Completed
- Set priority (LOW / MEDIUM / HIGH) and due date
- Stats bar (total, pending, done)
- Overdue task indicator

## Project Structure

```
src/main/java/com/taskmanager/
├── TaskManagerApplication.java      ← App entry point
├── SecurityConfig.java              ← Spring Security setup
├── model/
│   ├── User.java                    ← User entity (maps to users table)
│   └── Task.java                    ← Task entity (maps to tasks table)
├── repository/
│   ├── UserRepository.java          ← Database queries for User
│   └── TaskRepository.java          ← Database queries for Task
├── service/
│   ├── UserService.java             ← Register, find user logic
│   └── TaskService.java             ← CRUD logic for tasks
└── controller/
    ├── UserController.java          ← REST endpoints: /api/users
    └── TaskController.java          ← REST endpoints: /api/tasks

src/main/resources/
├── application.properties           ← DB config
└── static/
    ├── index.html                   ← Single-page frontend
    ├── css/style.css
    └── js/app.js

src/test/java/com/taskmanager/
└── TaskServiceTest.java             ← Unit tests (JUnit + Mockito)
```

## Setup & Run

### 1. Prerequisites
- Java 17+
- MySQL 8
- Maven 3.8+

### 2. Create the MySQL database
```sql
CREATE DATABASE taskmanager_db;
```

### 3. Update database credentials
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```
Spring Boot will auto-create the `users` and `tasks` tables on first run.

### 4. Run the app
```bash
mvn spring-boot:run
```
Open your browser: **http://localhost:8080**

### 5. Run tests
```bash
mvn test
```

---

## REST API Reference

### Users

| Method | Endpoint              | Description     |
|--------|-----------------------|-----------------|
| POST   | `/api/users/register` | Register user   |
| GET    | `/api/users/{id}`     | Get user by ID  |

**Register example:**
```json
POST /api/users/register
{
  "username": "john",
  "email": "john@example.com",
  "password": "secret123"
}
```

### Tasks

| Method | Endpoint                      | Description         |
|--------|-------------------------------|---------------------|
| GET    | `/api/tasks?userId=1`         | Get all tasks       |
| GET    | `/api/tasks/pending?userId=1` | Get pending tasks   |
| GET    | `/api/tasks/completed?userId=1`| Get completed tasks |
| GET    | `/api/tasks/{id}?userId=1`    | Get one task        |
| POST   | `/api/tasks`                  | Create task         |
| PUT    | `/api/tasks/{id}`             | Update task         |
| PATCH  | `/api/tasks/{id}/toggle?userId=1` | Toggle complete |
| DELETE | `/api/tasks/{id}?userId=1`    | Delete task         |

**Create task example:**
```json
POST /api/tasks
{
  "userId": 1,
  "title": "Buy groceries",
  "description": "Milk, eggs, bread",
  "priority": "HIGH",
  "dueDate": "2024-12-31"
}
```

---

## How to explain this project in an interview

> "I built a full-stack task management web app using Java Spring Boot for the backend
> and plain HTML/CSS/JS for the frontend. I designed a REST API with endpoints for
> user registration, authentication using Spring Security with BCrypt password hashing,
> and full CRUD operations for tasks. The data is persisted in MySQL using Spring Data
> JPA. I also wrote unit tests for the service layer using JUnit and Mockito."

**Key talking points:**
- MVC pattern: Model → Service → Controller
- BCrypt password hashing (never plain text)
- Spring Data JPA removes boilerplate SQL
- REST conventions: GET/POST/PUT/PATCH/DELETE
- Ownership check in TaskService (users can only see their own tasks)
- XSS prevention in frontend (escapeHtml function)
