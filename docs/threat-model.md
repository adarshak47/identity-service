# Threat Model

Threats:

1 Brute Force
Mitigation:
- Rate limiting
- Account lockout

2 JWT Theft
Mitigation:
- Short expiry
- Refresh rotation

3 Credential Stuffing
Mitigation:
- Login throttling

4 SQL Injection
Mitigation:
- Parameterized queries