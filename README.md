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



