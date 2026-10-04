
<div align="center">

# 🔥 StreakMate

**Learn Together. Stay Accountable.**

A social accountability platform where you build habits and reach learning goals through shared challenges, daily check-ins, streaks, badges, and leaderboards.

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.0-6DB33F?logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-UI-005F0F?logo=thymeleaf&logoColor=white)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white)

</div>

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Usage](#usage)
- [REST API](#rest-api)
- [Scheduled Jobs](#scheduled-jobs)
- [Testing](#testing)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [Author](#author)

## Overview

Habits stick when someone is watching. **StreakMate** lets users create or join time-boxed challenges (for example *"DSA 60-Day Challenge"* or *"Read 10 Pages Daily"*), check in every day, and compete on a friendly leaderboard. Streak tracking, achievement badges, and automated email reminders keep participants consistent.

## Features

| Area | What you get |
|------|--------------|
| 🔐 **Authentication** | Registration and login with Spring Security, BCrypt password hashing, and login by **email or username** |
| 🏁 **Challenges** | Create challenges with a title, description, and date range. Each one gets a unique **join code** to share with friends |
| ✅ **Daily check-ins** | One check-in per participant per day, with an optional note |
| 🔥 **Streaks** | Current and longest streak per challenge, plus a global streak across all challenges |
| 🏅 **Badges** | Awarded automatically for total check-in count and streak length |
| 📊 **Leaderboards** | Rank participants within each challenge |
| 🧑 **Profiles** | Personal dashboard and public profile pages (`/profile/{username}`) |
| 📧 **Email notifications** | Daily reminders for missed check-ins and a weekly progress summary (opt-in) |
| 🗂️ **Challenge history** | Browse past and expired challenges |
| 🛡️ **Error handling** | Global exception handler with custom 404 and 500 pages |

## Tech Stack

- **Backend:** Java 21, Spring Boot 3.5.0 (Web, Data JPA, Validation, Security, Mail)
- **Frontend:** Thymeleaf with the Layout Dialect, vanilla CSS and JavaScript
- **Database:** MySQL, with Hibernate schema auto-update
- **Build tool:** Maven
- **Utilities:** Lombok, Spring DevTools
- **Testing:** JUnit 5, Mockito (via `spring-boot-starter-test`)

## Architecture

The project follows a classic layered Spring MVC structure:

```
streakmate/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/streakmate/
    │   │   ├── config/        # Security, session constants, data loader
    │   │   ├── controller/    # MVC controllers + REST ApiController
    │   │   ├── dto/           # Request/response data transfer objects
    │   │   ├── exception/     # Custom exceptions + GlobalExceptionHandler
    │   │   ├── model/         # JPA entities
    │   │   ├── repository/    # Spring Data JPA repositories
    │   │   ├── scheduler/     # Cron jobs (reminders, badges, expiry)
    │   │   ├── service/       # Business logic
    │   │   └── StreakMateApplication.java
    │   └── resources/
    │       ├── static/        # css/main.css, js/main.js
    │       ├── templates/     # Thymeleaf views (auth, challenge, dashboard, profile, error)
    │       ├── application.properties
    │       └── application-dev.properties
    └── test/java/com/streakmate/service/   # Unit tests
```

### Domain model

- **User**: registered account (name, email, username, hashed password)
- **Challenge**: title, description, unique code, date range, status (`ACTIVE`, `COMPLETED`, `EXPIRED`, `ARCHIVED`), creator
- **ChallengeParticipant**: links a user to a challenge
- **DailyCheckIn**: a participant's check-in for a date, with an optional note
- **Badge** / **UserBadge**: badge definitions (`CHECK_IN_COUNT`, `STREAK_DAYS`, `CHALLENGE_COMPLETE`) and the ones each user has earned

## Getting Started

### Prerequisites

- **JDK 21** or later
- **Maven 3.9+**
- **MySQL 8+** running locally on port `3306`

### 1. Clone the repository

```bash
git clone https://github.com/AnusriHub/streakmate-habit-tracker.git
cd streakmate-habit-tracker/streakmate
```

### 2. Set up the database

The app creates the `streakmate_db` database automatically (`createDatabaseIfNotExist=true`) and the tables via Hibernate (`ddl-auto=update`). You only need a MySQL user with permission to create databases.

### 3. Set environment variables

```bash
# macOS / Linux
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password
```

```powershell
# Windows PowerShell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
```

### 4. Run the application

```bash
mvn spring-boot:run
```

Open **http://localhost:8080** in your browser.

To enable verbose SQL and debug logging, run with the dev profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 5. Build a runnable JAR (optional)

```bash
mvn clean package
java -jar target/streakmate-1.0.0.jar
```

> **Note:** Badge definitions are not seeded automatically. The sample-data seeder in `config/DataLoader.java` is currently commented out. To have badges awarded, uncomment it (it also creates demo users and challenges) or insert rows into the `badges` table yourself.

## Configuration

All settings live in `src/main/resources/application.properties`. Secrets are read from environment variables so they never get committed.

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | *(empty)* | MySQL password |
| `MAIL_USERNAME` | *(empty)* | SMTP username (Gmail address) |
| `MAIL_PASSWORD` | *(empty)* | SMTP password (use a Gmail **App Password**) |

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8080` | HTTP port |
| `app.base-url` | `http://localhost:8080` | Base URL used in emails |
| `app.mail.enabled` | `false` | Set to `true` to actually send emails. When `false`, emails are logged and skipped |

You can also keep local overrides in `application-local.properties` or `application-local.yml`, both of which are git-ignored.

### Enabling email

```bash
export MAIL_USERNAME=you@gmail.com
export MAIL_PASSWORD=your_app_password
# then set app.mail.enabled=true in application.properties
```

## Usage

1. **Register** an account, then **log in** with your email or username.
2. **Create a challenge** and share its join code, or **join** a friend's challenge with their code.
3. **Check in daily** from the challenge page and add a note about what you did.
4. Watch your **streak** grow, earn **badges**, and climb the **leaderboard**.
5. Visit your **profile** to see your stats and badges.

### Main routes

| Route | Access | Description |
|-------|--------|-------------|
| `/`, `/login`, `/register` | Public | Landing page and authentication |
| `/dashboard` | Authenticated | Your overview |
| `/challenges/create` | Authenticated | Create a challenge |
| `/challenges/join` | Authenticated | Join with a code |
| `/challenges/{id}` | Authenticated | Challenge detail and check-in |
| `/challenges/{id}/leaderboard` | Authenticated | Challenge leaderboard |
| `/challenges/history` | Authenticated | Past challenges |
| `/profile`, `/profile/{username}` | Authenticated | Your profile or another user's |

## REST API

All endpoints require an authenticated session and return `401 Unauthorized` otherwise.

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/users/{id}` | User details |
| `GET` | `/api/v1/users/{id}/activity` | User activity |
| `GET` | `/api/v1/users/{id}/streak` | User streak information |
| `GET` | `/api/v1/challenges/{id}` | Challenge details |
| `GET` | `/api/v1/challenges/{id}/leaderboard` | Challenge leaderboard |
| `GET` | `/api/v1/challenges/code/{code}` | Look up a challenge by join code |
| `GET` | `/api/v1/session/user` | Currently logged-in user |

## Scheduled Jobs

| Job | Schedule | Description |
|-----|----------|-------------|
| Daily reminders | 8:00 PM daily | Emails participants in active challenges who haven't checked in |
| Weekly summary | Sunday 9:00 AM | Sends each user a progress report |
| Badge processing | Midnight daily | Evaluates and awards badges |
| Challenge expiry | 1:00 AM daily | Marks past-due challenges as expired |

## Testing

```bash
mvn test
```

Current unit tests cover streak calculation (no check-ins, broken streaks, consecutive days, gaps, longest streak) and user registration (success, duplicate email, duplicate username).

## Roadmap

- [ ] Seed badges automatically on first startup
- [ ] Docker and Docker Compose setup
- [ ] Integration tests with Testcontainers
- [ ] Flyway or Liquibase database migrations
- [ ] Password reset flow
- [ ] Badges for completed challenges
- [ ] Charts and progress visualizations

## Contributing

Contributions are welcome.

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m "Add your feature"`
4. Push the branch: `git push origin feature/your-feature`
5. Open a Pull Request

## Author

**Anusri**: [@AnusriHub](https://github.com/AnusriHub)

---

<div align="center">
Built with ☕ and Spring Boot. Keep your streak alive. 🔥
</div>
