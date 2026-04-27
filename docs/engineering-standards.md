# Engineering Standards

---

# 1. General Principles

Code should be:

- Readable
- Maintainable
- Testable
- Secure
- Simple

Follow:
- SOLID principles
- Single Responsibility
- Clean Code principles

---

# 2. Naming Conventions

## Classes

Use intention-revealing names.

Good:

AuthenticationService
JwtTokenProvider
UserAccountRepository

Avoid:

AuthUtil
CommonHelper
ManagerService

---

## Methods

Use verb + noun.

Examples:

registerUser()
authenticateUser()
generateAccessToken()

Avoid:

process()
handle()
doStuff()

---

## Variables

Use descriptive names.

Good:

hashedPassword
refreshTokenExpiry

Avoid:

data
temp
obj

---

# 3. Layering Rules

## Controller
Only:
- request handling
- response mapping

No business logic.

---

## Service
Contains business logic.

---

## Repository
Persistence only.

---

# 4. Dependency Injection

Use:

Constructor injection only.

Never use:

@Autowired field injection

---

# 5. DTO Rules

Use DTOs for API contracts.

Never expose JPA entities in APIs.

Naming:

RegisterRequest
LoginResponse

---

# 6. Exception Handling

Use centralized exception handling.

Standard error format:

{
"code":"ERROR_CODE",
"message":"Human readable message"
}

Never expose stack traces.

---

# 7. API Conventions

Base path:

/api/v1/

Resources use nouns.

Good:

/users
/auth/login

Avoid:

/doLogin

---

# 8. Security Rules

Passwords:
- Never log passwords
- Hash using BCrypt

JWT:
- Never store sensitive data in claims

Secrets:
- Never commit secrets

---

# 9. Database Standards

- Use Flyway migrations only
- No manual schema changes
- Use UUID primary keys
- Audit columns required

---

# 10. Testing Standards

Coverage target:
80%+

Must have:
- Unit tests
- Integration tests

---

# 11. Logging

Use structured logs.

Never log:
- passwords
- tokens
- secrets

---

# 12. Package Structure

Feature-based packaging only.

Avoid package-by-layer root structure.

---

# 13. Git Standards

Commit convention:

feat:
fix:
refactor:
docs:
test:
chore:
