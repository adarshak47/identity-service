# Functional Requirements

FR-001 User Registration

System shall allow user registration using:
- email
- password

---

FR-002 Login

System shall authenticate user and issue:
- access token
- refresh token

---

FR-003 Refresh Token

System shall issue new access token from valid refresh token.

---

FR-004 Logout

System shall invalidate user session.

---

# Non Functional Requirements

Security:
- Password hashing
- JWT signing
- Secure secret storage

Performance:
- Login latency <300ms

Scalability:
- Stateless architecture

Availability:
- 99.9 uptime target
