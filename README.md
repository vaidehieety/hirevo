# Hirevo

A job platform built for freshers. It explains why you match a job, warns you
about fake job posts, and holds recruiters accountable for replying.

## The problem
<!-- Rewrite this section in YOUR own words (see the tip below). -->
Freshers apply to hundreds of jobs and rarely hear back. When they're rejected,
they never learn which skills were missing. Some job posts are scams asking for
"registration fees", and many applications sit in "Applied" forever because
recruiters simply stop responding.

## What Hirevo will do (planned)
- **AI Match Score + Skill-Gap Roadmap**: a 0–100 score for any job, the skills
  you have and miss, and a 3-step plan to close the gap
- **Fake-Job Shield**: suspicious job posts get flagged for an admin to review
- **Anti-Ghosting**: applications with no recruiter update for 14 days
  auto-close, and the company's public Response Score drops

## Status
✅ **v0.1 — Jobs API:** create, search, update and close jobs, with request
validation and clean error responses. Data lives in memory for now
(MySQL arrives in v0.2).

🌍 **Live demo:** https://hirevo-api.onrender.com/actuator/health
(free hosting: the first request after a quiet spell takes ~1 minute to wake up)

## Run it locally
Requires Java 21.

```bash
cd hirevo
./mvnw spring-boot:run
```

Then open http://localhost:8080/actuator/health → `{"status":"UP"}`

## API (v0.1)
| Method | Path | Purpose |
|---|---|---|
| GET | `/api/v1/jobs?skill=&location=` | Search open jobs |
| GET | `/api/v1/jobs/{id}` | Job details |
| POST | `/api/v1/jobs` | Post a job |
| PUT | `/api/v1/jobs/{id}` | Update a job |
| PATCH | `/api/v1/jobs/{id}/close` | Close a job |

## Tech stack (planned)
Java 21 · Spring Boot 3.5 · MySQL · MongoDB Atlas · Spring AI + Gemini ·
Redis · Kafka · Docker · GitHub Actions
