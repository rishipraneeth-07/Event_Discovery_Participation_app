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

### Events Screen

<img width="683" height="1456" alt="Screenshot 2026-04-23 013144" src="https://github.com/user-attachments/assets/20627657-3470-487b-bea1-c73fa996f5fa" />


### Registrations Screen

<img width="699" height="1444" alt="Screenshot 2026-04-23 013236" src="https://github.com/user-attachments/assets/7d1d3d4a-f16f-4d28-b37c-73a55bb4904f" />


### Notification Screen

<img width="694" height="1439" alt="Screenshot 2026-04-23 013304" src="https://github.com/user-attachments/assets/7ea589ca-c9e4-447b-8c15-b8fe2ce63802" />


### Organizer Dashboard

<img width="687" height="1459" alt="Screenshot 2026-04-23 013417" src="https://github.com/user-attachments/assets/ff267c04-a9dc-4859-b446-bedae27cd128" />


### Organizer events


<img width="685" height="1457" alt="Screenshot 2026-04-23 013505" src="https://github.com/user-attachments/assets/2da729ce-ed72-485a-a05b-b17b82285da9" />


### Admin Panel screen

<img width="680" height="1451" alt="Screenshot 2026-04-23 013554" src="https://github.com/user-attachments/assets/5fca5131-1642-4acd-a24c-14bbfbeeedcf" />











