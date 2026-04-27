# Identity Service

Centralized Authentication and Authorization service built using Java and Spring Boot following production-grade engineering practices and microservice architecture principles.

---

## Overview

Identity Service provides:

- User Registration
- Authentication (Login)
- JWT Access Token issuance
- Refresh Token management
- Logout / Session termination
- Role-based authorization (planned)
- Audit logging (planned)

This service is designed as a reusable identity platform for multiple applications.

---

## Goals

This project is built to:

- Design a production-grade authentication microservice
- Practice backend system design
- Implement security best practices
- Build reusable identity infrastructure
- Prepare for backend/microservices interviews

---

## Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA

### Database
- PostgreSQL

### Database Migration
- Flyway

### Caching (Planned)
- Redis

### Messaging (Planned)
- Kafka

### Documentation
- OpenAPI / Swagger

### Testing
- JUnit 5
- Testcontainers

### Containerization
- Docker

---

## Architecture Principles

This service follows:

- Stateless authentication
- API-first design
- Feature-based packaging
- Domain-driven boundaries
- Versioned database migrations
- Secure coding principles

---

## Project Structure

```text
identity-service/
├── docs/
├── src/
├── docker/
├── scripts/
├── Dockerfile
├── docker-compose.yml
└── README.md
```

---

## Package Structure

```text
com.adarsh.identity

auth/
user/
security/
common/
```

---

## Planned Features

### Phase 1
- User Registration
- Login
- JWT Authentication
- Refresh Tokens
- Logout

### Phase 2
- RBAC
- Permissions
- Method-level Authorization

### Phase 3
- API Gateway integration
- Service-to-service security

### Phase 4
- OAuth2 / OIDC
- Social login
- Audit events

---

## API Endpoints (Planned)

```http
POST /api/v1/auth/register
POST /api/v1/auth/login
POST /api/v1/auth/refresh
POST /api/v1/auth/logout
GET  /api/v1/users/me
```

---

## Development Workflow

Branch strategy:

- main
- develop
- feature/*

Example:

```bash
feature/user-registration
feature/login-api
```

Commit format:

```bash
feat: add registration endpoint
fix: handle token expiry
chore: bootstrap project
```

---

## Documentation

Project documentation lives under:

```text
docs/
```

Contains:

- Requirements
- Architecture
- ADRs
- Threat Model
- Engineering Standards

---

## Current Status

Current Milestone:

v0.1-foundation (In Progress)

---

## Future Enhancements

- Redis token revocation
- Kafka audit events
- OAuth2 Authorization Server
- Multi-tenant identity support

---

## Author

Backend engineering project focused on production-grade authentication system design.
