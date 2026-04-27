# Architecture

## High-Level Design

Client
|
API Layer
|
Auth Service
|- Security
|- Business Logic
|- Persistence

Database:
PostgreSQL

Future:
Redis
Kafka

---

## Core Components

- Authentication Module
- Token Module
- User Module
- Security Module

---

## Service Responsibilities

Owns:
- Credentials
- Tokens
- Authorization metadata

Does not own:
- Orders
- Payments
- Business domain data
