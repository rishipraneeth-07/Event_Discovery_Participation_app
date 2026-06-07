# EventApp Backend

- A RESTful backend for a college event management platform built with Spring Boot 4, MySQL, and JWT-based stateless authentication. It supports three user roles — Student, Organizer, and Admin — with a full event lifecycle including creation, moderation, registration, recommendations, and notifications.


## Tech Stack 

| Layer | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.0.3 |
| Security | Spring Security + JWT (stateless) |
| Database | MySQL 8 |
| Migrations | Flyway |
| ORM | Spring Data JPA (Hibernate) |
| Validation | Jakarta Bean Validation |
| Email | Spring Mail (Gmail SMTP) |
| Build Tool | Maven |
| Utilities | Lombok |

## Features
 
- **Authentication** — Register, login, forgot/reset password via email token
- **Role-based access control** — `STUDENT`, `ORGANIZER`, `ADMIN` roles with endpoint-level enforcement
- **Event management** — Create, update, delete, search, filter, paginate events
- **Event moderation** — Organizers submit events; Admins approve or reject with a moderation log
- **Registrations** — Students register/cancel event registration; organizers view attendees
- **Saved events** — Bookmark events for later
- **Recently viewed** — Track recently viewed events per user
- **Recommendations** — Interest-based event recommendations
- **Notifications** — In-app notifications with configurable preferences
- **Admin dashboard** — Stats, user management, event moderation controls
- **App content** — Serve dynamic app-level content via API


## Application Flow

<img width="1365" height="1160" alt="Screenshot 2026-02-08 125845" src="https://github.com/user-attachments/assets/3a2fc75b-cb45-4c70-8d1b-099016e03c65" />

## Screens

### Login Screen

<img width="690" height="1472" alt="Screenshot 2026-04-23 012420" src="https://github.com/user-attachments/assets/a4c1db4c-697c-4312-966a-bb34cd4b914b" />

### Register Screen

<img width="682" height="1469" alt="Screenshot 2026-04-23 012512" src="https://github.com/user-attachments/assets/917cf557-e03b-4eaf-baac-e16db425552c" />

### Student Dashboard

<img width="691" height="1466" alt="Screenshot 2026-04-23 013054" src="https://github.com/user-attachments/assets/98c27535-bf14-4d08-af4e-6ff4f288abe8" />









