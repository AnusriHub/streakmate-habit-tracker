# 🔥 StreakMate

> **Learn Together. Stay Accountable.**

StreakMate is a social accountability platform where users create learning or habit challenges, invite friends using a unique challenge code, and stay consistent through streaks, leaderboards, and daily check-ins.

---

## 📸 Screenshots

| Landing Page | Dashboard | Challenge Detail |
|---|---|---|
| *(see /screenshots)* | *(see /screenshots)* | *(see /screenshots)* |

---

## ✨ Features

### Core
- **User Registration** — name, email, unique username; no password (demo-friendly)
- **Challenge Creation** — title, description, date range; auto-generates a share code like `DSA60`
- **Join with Code** — any user can join any challenge using its code
- **Daily Check-In** — one check-in per day per challenge; duplicate prevention enforced
- **Streak Tracking** — current streak + longest streak, calculated from check-in history
- **Leaderboard** — per-challenge ranking by completion % then current streak
- **Challenge History** — all challenges with status (Active / Expired / Completed)
- **Profile Page** — stats, badges, full challenge list

### Bonus
- **Achievement Badges** — 🌱 First Step, ⭐ Getting Started, 🏆 Dedicated, 💎 Legend, 🔥 Consistent, ⚡ Unstoppable
- **Email Reminders** — daily 8 PM email to users who haven't checked in (configurable)
- **Weekly Report** — Sunday 9 AM email with streak, completion rate, leaderboard rank
- **REST API** — full JSON API at `/api/v1/`
- **Sample Data** — 3 users, 3 challenges, 20+ check-ins auto-loaded on first start

---

## 🏗 Architecture

```
MVC Architecture (Spring Boot)
┌─────────────┐     ┌─────────────┐     ┌──────────────┐
│  Controller │────▶│   Service   │────▶│  Repository  │
│  (Web/REST) │     │  (Business  │     │  (Spring     │
│             │     │   Logic)    │     │   Data JPA)  │
└─────────────┘     └─────────────┘     └──────────────┘
       │                                        │
       ▼                                        ▼
  Thymeleaf                                  MySQL 9
  Templates                               (via Hibernate)

Scheduler ──▶ EmailService ──▶ JavaMailSender ──▶ SMTP
```

---

## 🗄 Database Schema

```sql
users
  id, name, email (unique), username (unique), created_at

challenges
  id, title, description, challenge_code (unique),
  start_date, end_date, status, created_at, creator_id → users.id

challenge_participants
  id, user_id → users.id, challenge_id → challenges.id, joined_at
  UNIQUE (user_id, challenge_id)

daily_check_ins
  id, participant_id → challenge_participants.id,
  check_in_date, note, completed, created_at
  UNIQUE (participant_id, check_in_date)

badges
  id, badge_name (unique), description, icon, threshold, type

user_badges
  id, user_id → users.id, badge_id → badges.id, earned_at
  UNIQUE (user_id, badge_id)
```

---

## ⚙️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL 9 |
| Templates | Thymeleaf 3.1 |
| Frontend | Bootstrap 5.3, Vanilla JS |
| Mail | Jakarta Mail (JavaMailSender) |
| Scheduler | Spring `@Scheduled` |
| Validation | Jakarta Validation |
| Build | Maven |
| Testing | JUnit 5 + Mockito |
| Utilities | Lombok |

---

## 🚀 Setup & Run

### Prerequisites
- Java 21+
- MySQL 9+ running locally
- Maven 3.9+

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/streakmate.git
cd streakmate
```

### 2. Create MySQL database
```sql
CREATE DATABASE streakmate_db;
```
*(The app auto-creates tables via `spring.jpa.hibernate.ddl-auto=update`)*

### 3. Configure `application.properties`
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/streakmate_db
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

### 4. (Optional) Enable email
```properties
app.mail.enabled=true
spring.mail.username=your-gmail@gmail.com
spring.mail.password=your-app-password
```
> Use a Gmail App Password — not your regular password.  
> Enable 2FA → Google Account → Security → App Passwords.

### 5. Run
```bash
mvn spring-boot:run
```

### 6. Open browser
```
http://localhost:8080
```

### Demo accounts (auto-loaded)
| Username | Name |
|---|---|
| `alice_j` | Alice Johnson |
| `bobsmith` | Bob Smith |
| `priya_s` | Priya Sharma |

### Demo challenge codes
| Code | Challenge |
|---|---|
| `DSA60` | DSA 60-Day Challenge |
| `GERMAN30` | Learn German in 30 Days |
| `READING21` | Read 10 Pages Daily |

---

## 📡 REST API

Base URL: `http://localhost:8080/api/v1`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/users` | List all users |
| GET | `/users/{id}` | Get user by ID |
| GET | `/users/{id}/streak` | Get current + longest streak |
| GET | `/users/{id}/activity` | Recent check-ins |
| GET | `/challenges/{id}` | Get challenge |
| GET | `/challenges/code/{code}` | Get challenge by code |
| GET | `/challenges/{id}/leaderboard` | Leaderboard entries |
| GET | `/session/user` | Current logged-in user |

---

## 🧠 Key Algorithms

### Streak Calculation (StreakService)
```
1. Fetch all check-ins ordered by date DESC
2. If latest check-in is not today or yesterday → streak = 0
3. Count consecutive days backward from latest check-in
4. Stop at first gap → that's the current streak
```

### Leaderboard Ranking (ChallengeService)
```
1. Fetch all participants for the challenge
2. Per participant: count completed days, calculate streaks
3. completion% = completed / totalDays * 100
4. Sort by: completion% DESC, then currentStreak DESC
5. Assign rank 1, 2, 3...
```

### Challenge Code Generation
```
1. Strip non-letters from title, uppercase, take first 8 chars
2. Append total days: "LEARNGERMAN" + "30" = "LEARNGERMAN30"
3. Check uniqueness; append suffix if collision: "LEARNGERMAN301"
```

---

## 📅 Scheduler Jobs

| Cron | Job | Description |
|---|---|---|
| `0 0 20 * * *` | Daily Reminder | Emails users who haven't checked in by 8 PM |
| `0 0 9 * * SUN` | Weekly Report | Sunday morning streak summary |
| `0 0 0 * * *` | Badge Processing | Awards badges based on milestones |
| `0 0 1 * * *` | Expire Challenges | Marks past-end-date challenges as EXPIRED |

---

## 🧪 Running Tests

```bash
mvn test
```

Tests cover:
- `StreakServiceTest` — streak algorithm edge cases (no check-ins, gaps, consecutive runs)
- `UserServiceTest` — registration validation (duplicate email, duplicate username)

---

## 🗂 Project Structure

```
src/main/java/com/streakmate/
├── config/
│   ├── DataLoader.java          ← Sample data (CommandLineRunner)
│   └── SessionConfig.java       ← Session key constants
├── controller/
│   ├── AuthController.java      ← /register, /login, /logout
│   ├── DashboardController.java ← /dashboard
│   ├── ChallengeController.java ← /challenges/**
│   ├── ProfileController.java   ← /profile/**
│   └── ApiController.java       ← /api/v1/**
├── dto/
│   ├── UserRegistrationDto.java
│   ├── ChallengeCreateDto.java
│   ├── CheckInDto.java
│   ├── LeaderboardEntryDto.java
│   └── DashboardDto.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── DuplicateResourceException.java
│   └── InvalidOperationException.java
├── model/
│   ├── User.java
│   ├── Challenge.java           ← ChallengeStatus enum inside
│   ├── ChallengeParticipant.java
│   ├── DailyCheckIn.java
│   ├── Badge.java               ← BadgeType enum inside
│   └── UserBadge.java
├── repository/
│   ├── UserRepository.java
│   ├── ChallengeRepository.java
│   ├── ChallengeParticipantRepository.java
│   ├── DailyCheckInRepository.java
│   ├── BadgeRepository.java
│   └── UserBadgeRepository.java
├── scheduler/
│   └── StreakMateScheduler.java ← 4 scheduled jobs
├── service/
│   ├── UserService.java
│   ├── ChallengeService.java    ← Core business logic
│   ├── StreakService.java        ← Streak algorithm
│   ├── BadgeService.java
│   ├── DashboardService.java
│   └── EmailService.java
└── StreakMateApplication.java

src/main/resources/
├── templates/
│   ├── fragments/layout.html    ← Shared navbar
│   ├── landing.html
│   ├── auth/{register,login}.html
│   ├── dashboard/dashboard.html
│   ├── challenge/{create,join,detail,history}.html
│   ├── profile/profile.html
│   └── error/{404,500,error}.html
├── static/
│   ├── css/main.css             ← Full design system
│   └── js/main.js
└── application.properties
```

---

## 🔮 Future Improvements

- [ ] Password-based authentication (BCrypt)
- [ ] Challenge categories / tags
- [ ] Push notifications (PWA)
- [ ] Friend system + challenge invitations via email
- [ ] Calendar heatmap view (like GitHub contributions)
- [ ] Mobile app (React Native)
- [ ] Docker Compose setup
- [ ] Cloud deployment (Railway / Render)

---

## 💡 Interview Talking Points

| Topic | Where it's used |
|---|---|
| MVC Architecture | Controllers → Services → Repositories → Views |
| JPA Relationships | `@OneToMany`, `@ManyToOne`, `@JoinColumn`, `@UniqueConstraint` |
| Streak Algorithm | `StreakService` — consecutive date comparison |
| Scheduler | `@Scheduled` in `StreakMateScheduler` — 4 cron jobs |
| Email Flow | `@Async` + `JavaMailSender` + SMTP |
| Leaderboard Logic | Sort by completion%, tiebreak on streak |
| Validation | Jakarta `@Valid`, `BindingResult`, custom messages |
| Exception Handling | `@ControllerAdvice` + `GlobalExceptionHandler` |
| REST API | `@RestController` at `/api/v1/**` |
| Thymeleaf | Server-side rendering, fragments, form binding |
| Constructor Injection | `@RequiredArgsConstructor` (Lombok) — no field injection |
| Records | Used for immutable response DTOs where appropriate |

---

## 👩‍💻 Author

Built as a portfolio project for Java Full-Stack SDE internship applications.

---

*StreakMate — Learn Together. Stay Accountable.*
