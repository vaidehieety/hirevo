# Hirevo — Design

<!-- Hirevo's plan on one page: what we're building, for whom, what data, and which URLs.
     Written BEFORE any code (Java Backend Notes, Chapter 3). Update it whenever a decision changes. -->

> **Status:** Foundations (planning). This is the plan that versions v0.1 → v1.1 follow.
> Nothing below is built yet.

---

## 1. Problem statement

<!-- One paragraph: who has what problem, and why it matters. -->

Freshers apply to hundreds of jobs but rarely get responses or feedback, and some postings are
scams. Hirevo gives candidates an explainable match score and a skill-gap roadmap, flags
suspicious jobs, and holds recruiters accountable for responding.

---

## 2. Users (roles)

| Role | What they do |
|---|---|
| **CANDIDATE** | Builds a profile, uploads a resume, searches jobs, sees match score + roadmap, applies, tracks applications |
| **RECRUITER** | Belongs to one company, posts jobs, views AI-ranked applicants, moves applications through stages |
| **ADMIN** | Verifies companies, reviews flagged (possibly fake) jobs, sees platform stats |

---

## 3. Functional requirements (what the system does)

<!-- Features. Each one becomes endpoints and tests later. -->

1. Users register and log in as CANDIDATE or RECRUITER; admins are created by the system.
2. Recruiters create a company profile; admins verify companies.
3. Recruiters post, edit and close jobs with required skills.
4. Anyone can search open jobs by keyword, skill, location, work mode and experience — in pages.
5. Candidates upload a PDF resume (max 2 MB); the system extracts its text.
6. Candidates see an AI match score, matched/missing skills and a 3-step roadmap for a job.
7. Candidates apply once per job and can withdraw.
8. Recruiters see applicants ranked by fit and move them through stages.
9. Applications with no update for 14 days auto-close; the company's Response Score reflects it.
10. New job posts are checked for fraud; risky ones are FLAGGED for admin review (flag, never delete).
11. Candidates get an email on every status change.

---

## 4. Non-functional requirements (how well it does it)

<!-- Qualities. Notice the last column: these decide the architecture. -->

| Quality | Target for Hirevo | What it forces us to build |
|---|---|---|
| Performance | Job search p95 < 300 ms; apply < 500 ms | Indexes, caching, pagination, AI work in the background |
| Scalability | Handle more users by adding servers | Stateless app servers (JWT, no in-memory sessions) |
| Availability | Applying works even if the AI provider is down | Timeouts, circuit breaker, fallback |
| Security | Hashed passwords; no one reads others' data | BCrypt, JWT, role + ownership checks |
| Reliability | No duplicate applications; no lost emails | Unique constraints, events with retries |
| Maintainability | Easy to change safely | Layers, tests, migrations, API docs |
| Observability | See what's happening in production | Health checks, metrics, request IDs in logs |
| Cost | Keep AI bills small | Cache AI results; call the AI model only on demand |

---

## 5. User stories (with acceptance criteria)

<!-- Format: As a <role>, I want <goal>, so that <benefit>.
     Acceptance criteria are testable rules; they become test cases in Chapter 22. -->

### Story 1 — Apply to a job

As a **candidate**, I want to apply to a job, so that the recruiter can consider me.

- Given I'm logged in as CANDIDATE and the job is OPEN → an application is created with status APPLIED → `201 Created`.
- Given I already applied → `409 Conflict`.
- Given the job is CLOSED or FLAGGED → `422` with a clear message.
- Given I have no resume uploaded → `422` "Upload a resume first".
- After applying, the recruiter's dashboard shows me, and I get a confirmation email.

### Story 2 — Post a job

As a **recruiter**, I want to post a job for my company, so that candidates can find and apply to it.

- Given I'm logged in as RECRUITER and the request is valid → the job is saved with status OPEN → `201 Created` with a `Location` header.
- Given the title is blank, or the minimum salary is higher than the maximum → `400` listing each invalid field.
- Given I'm logged in as CANDIDATE → `403 Forbidden`.
- Given the Fake-Job Shield rates the post as risky (risk score ≥ 0.7) → status FLAGGED; it stays out of search until an admin reviews it.

### Story 3 — See my match score

As a **candidate**, I want to see my match score and missing skills for a job, so that I know whether to apply and what to learn next.

- Given I'm logged in as CANDIDATE with a resume uploaded → `200 OK` with a score from 0 to 100, my matched skills, my missing skills and a 3-step roadmap.
- Given I have no resume uploaded → `422` "Upload a resume first".
- Given the job doesn't exist → `404 Not Found`.
- Given I ask more than 20 times in one hour → `429 Too Many Requests`.
- Asking again for the same job within 24 hours returns the saved report (no new AI call).

---

## 6. Data: entities and relationships

<!-- Entities = the nouns in the requirements. Relationships = "how many of B can one A have?" -->

| Entity | Key fields | Relationships |
|---|---|---|
| **User** | email, passwordHash, fullName, role | Recruiter → many-to-one Company |
| **Company** | name, website, verified, responseScore | One-to-many Jobs, one-to-many Recruiters |
| **Job** | title, description, location, workMode, experience range, salary range, status, riskScore | Many-to-one Company; many-to-many Skills; one-to-many Applications |
| **Skill** | name (unique) | Many-to-many Jobs |
| **Application** | status, lastStatusChangeAt, matchScore, rejectionReason | Many-to-one Candidate (User); many-to-one Job; one-to-many StatusHistory; unique (candidate, job) |
| **StatusHistory** | fromStatus, toStatus, changedBy, changedAt | Many-to-one Application |
| **Resume** (MongoDB) | candidateId, fileName, text, parsed skills, embedding | Linked by candidateId |

- One company has many jobs; a job belongs to one company → **one-to-many**.
- A job needs many skills; a skill appears in many jobs → **many-to-many** (needs a link table).
- A candidate has many applications; each application belongs to one candidate → **one-to-many**.

**Why Resume lives in MongoDB:** its shape varies from resume to resume, and it stores an
embedding vector for AI search. Everything with strict relationships and transactions stays in MySQL.

### Application lifecycle (the heart of the domain)

```
APPLIED → VIEWED → SHORTLISTED → INTERVIEW → OFFERED
```

- From any open stage, a recruiter can move an application to **REJECTED** (with an optional reason).
- No status change for 14 days → a nightly job marks it **AUTO_CLOSED**, and the company's Response Score drops.
- A candidate can **WITHDRAW** any time before an offer.
- Invalid jumps (like APPLIED → OFFERED) are refused by the code — this is a **state machine**.

---

## 7. API (designed before the code)

<!-- Resources are plural nouns, versioned under /api/v1. The HTTP method is the verb.
     {id} = a placeholder for a real ID. "owner RECRUITER" = a recruiter whose company owns the job. -->

| Method | Path | Who | Purpose |
|---|---|---|---|
| POST | `/api/v1/auth/register` | public | Register candidate/recruiter |
| POST | `/api/v1/auth/login` | public | Get access + refresh token |
| POST | `/api/v1/auth/refresh` | public | New access token |
| POST | `/api/v1/companies` | RECRUITER | Create company |
| PATCH | `/api/v1/admin/companies/{id}/verify` | ADMIN | Verify company |
| GET | `/api/v1/jobs` | public | Search jobs (filters + paging) |
| GET | `/api/v1/jobs/{id}` | public | Job details |
| POST | `/api/v1/jobs` | RECRUITER | Post job |
| PUT | `/api/v1/jobs/{id}` | owner RECRUITER | Update job |
| PATCH | `/api/v1/jobs/{id}/close` | owner RECRUITER | Close job |
| POST | `/api/v1/candidates/me/resume` | CANDIDATE | Upload resume (PDF) |
| GET | `/api/v1/jobs/{id}/match` | CANDIDATE | AI match score + roadmap |
| POST | `/api/v1/jobs/{id}/applications` | CANDIDATE | Apply |
| GET | `/api/v1/applications/me` | CANDIDATE | My applications |
| POST | `/api/v1/applications/{id}/withdraw` | CANDIDATE | Withdraw |
| GET | `/api/v1/jobs/{id}/applications` | owner RECRUITER | Applicants ranked by fit |
| PATCH | `/api/v1/applications/{id}/status` | owner RECRUITER | Move stage |
| GET | `/api/v1/admin/jobs/flagged` | ADMIN | Suspicious jobs |
| PATCH | `/api/v1/admin/jobs/{id}/review` | ADMIN | Approve/reject flagged job |

---

## 8. Tech choices (one-line reasons)

- **Java 21 + Spring Boot** — long-term support, and the most-hired Java backend stack.
- **MySQL** — relational data, joins, transactions, constraints.
- **MongoDB Atlas** — flexible resume documents + built-in vector search.
- **Spring AI + Google Gemini** — one Java API for AI chat and embeddings; Gemini has a free tier.
- **Redis** — sub-millisecond cache and rate limiting.
- **Kafka** — moves slow side effects (email, AI scoring) out of the request.

---

## 9. Release plan (MVP first, one version at a time)

<!-- Each version is built, tagged (git tag v0.x.0) and deployed. v0.1 is the "walking skeleton". -->

| Version | Name | What it adds |
|---|---|---|
| — | Foundations | Plan, tools, GitHub repo (nothing deployed yet) |
| v0.1 | Jobs API | Spring Boot app, REST API for jobs, validation, errors (in memory) — first deploy |
| v0.2 | Real Data | MySQL, JPA, relationships, search, the apply flow, status machine, migrations |
| v0.3 | Secure | Register/login, JWT, roles, ownership checks |
| v0.4 | Smart | Request IDs (AOP), MongoDB, AI match score + roadmap, Fake-Job Shield (Gemini) |
| v0.5 | Complete | Resume upload, emails, anti-ghosting job, Response Score |
| v0.6 | Fast | Redis caching, rate limiting, instant logout |
| v0.7 | Event-Driven | Kafka, retries, dead-letter topics — and my own server |
| v0.8 | Proven | Tests, Swagger docs, clean-up |
| v0.9 | Distributed | Split into microservices |
| v1.0 | Production | Docker explained, CI/CD, automatic deploys, monitoring |
| v1.1 | Upgraded | Spring Boot 4 + Spring AI 2.0 |

---

## 10. Checklist before coding any endpoint

1. **Input** — what does the client send? Which fields are required?
2. **Validation** — what's invalid? (format, ranges, lengths)
3. **Who** — which roles can call it? Can they only touch their own data?
4. **Business rules** — what must be true? (job OPEN, not already applied)
5. **Data** — which tables/documents change? Inside one transaction?
6. **Output** — what does success return? Which status code?
7. **Failures** — what can go wrong, and which error code for each?
8. **Side effects** — emails, events, cache eviction, audit log?
9. **Performance** — pagination? index needed? cache?
10. **Tests** — the happy path + each failure from #7.
