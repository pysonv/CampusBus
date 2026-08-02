# CampusBus Backend

This is the backend for the Smart Campus Bus Management and Automated Vehicle Recognition System.
It is built with Java 21 and Spring Boot.

## Prerequisites

- **Java 21**
- **Maven**
- **MySQL** installed and running

## Database Setup

Before running the application, ensure your MySQL server is running and create the database:

```sql
CREATE DATABASE campusbus;
```

## Required Environment Variables

You must configure the following environment variables (or specify them in your IDE) before starting the application:

- `DB_URL` - (default: `jdbc:mysql://localhost:3306/campusbus`)
- `DB_USERNAME` - (default: `root`)
- `DB_PASSWORD` - (must be provided, no default)
- `FRONTEND_URL` - (default: `http://localhost:5173`)

## How to Run

Navigate to the `backend` directory and use the Maven wrapper (if generated) or your local Maven installation:

```bash
./mvnw spring-boot:run
```

Or package it:

```bash
./mvnw clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

## Health Endpoint

Once the application is running, you can verify it via the health endpoint.

**Request:**
```http
GET http://localhost:8080/api/health
```

**Expected Response (200 OK):**
```json
{
  "status": "UP",
  "application": "CampusBus Backend"
}
```
