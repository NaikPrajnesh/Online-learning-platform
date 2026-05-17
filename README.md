# EduLearn – Online Learning Platform

Full-stack e-learning web application built with **Java Spring MVC** (Spring Boot), **HTML/CSS/JavaScript**, and optional **MySQL**.

## Features

- Homepage with featured courses
- Course catalog with search and category filters
- Course detail pages with lessons, video player, and progress tracking
- My Courses dashboard
- Lesson completion checkboxes with dynamic progress percentage
- **LocalStorage** persistence for enrollments and progress (survives refresh)
- Mock authentication (signup/login with HTTP session)
- Admin panel to add, edit, and delete courses
- Downloadable completion certificates (print to PDF)
- Dark/light theme toggle
- Fully responsive layout (Flexbox, Grid, media queries)

## Prerequisites

- Java 17+
- Maven 3.8+

Optional for MySQL:

- XAMPP or MySQL 8+
- Create database `elearn` (auto-created if using `createDatabaseIfNotExist`)

## Quick start

```bash
cd onlinelearningplatform
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080)

### Demo accounts

| Role    | Email               | Password   |
|---------|---------------------|------------|
| Student | student@elearn.com  | student123 |
| Admin   | admin@elearn.com    | admin123   |

## MySQL (optional)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

Update `src/main/resources/application-mysql.properties` with your MySQL username and password.

## API endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/courses` | List courses (`?search=&category=`) |
| GET | `/api/courses/featured` | Featured courses |
| GET | `/api/courses/{id}` | Course detail |
| POST | `/api/auth/signup` | Register |
| POST | `/api/auth/login` | Login |
| POST | `/api/enrollments` | Enroll (session required) |
| GET | `/api/enrollments/my` | My enrollments |
| GET | `/api/progress/{courseId}` | Progress |
| PUT | `/api/progress/{courseId}/lessons/{lessonId}` | Toggle lesson |

## Project structure

```
src/main/java/com/elearn/platform/   # Spring MVC controllers, services, repositories
src/main/resources/static/           # HTML, CSS, JS frontend
src/main/resources/schema.sql        # Database schema
src/main/resources/data.sql          # Seed data
```

## Notes

- Enrollments and progress work **offline** via LocalStorage; logging in syncs with the backend.
- Default profile uses embedded H2—no database setup required.
