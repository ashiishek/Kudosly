# Backend - Kudosly

Spring Boot backend for Kudosly employee recognition platform.

## Prerequisites

- Java 17+
- Maven 3.8+
- MongoDB 6.0+

## Configuration

Set environment variables:

```bash
export OPENAI_API_KEY=your_openai_key
```

## Build

```bash
mvn clean install
```

## Run

```bash
mvn spring-boot:run
```

API will be available at `http://localhost:8080`

## API Endpoints

### Efforts
- POST `/api/events` - Submit effort event

### Recognitions
- GET `/api/recognition/{employeeId}` - Get all recognitions
- GET `/api/recognition/{employeeId}/recent` - Get recent recognitions

### Badges
- GET `/api/badges` - Get all badges
- GET `/api/badges/{employeeId}` - Get employee badges

### Digest
- GET `/api/digest/{employeeId}` - Get weekly digest
- POST `/api/digest/{employeeId}/generate` - Generate new digest

### User Feed
- GET `/api/user/{employeeId}/feed` - Get complete user feed

## Architecture

- **Effort Intake Service** - Processes incoming events
- **AI Analyzer Service** - Classifies and scores efforts
- **Recognition Generator** - Creates personalized messages
- **Weekly Digest Service** - Generates weekly summaries
