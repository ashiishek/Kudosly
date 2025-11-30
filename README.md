# Kudosly - AI-Powered Employee Recognition Platform

**Tagline:** "Invisible Effort → Visible Appreciation"

Kudosly is an AI-powered employee recognition platform designed to surface invisible contributions and turn them into meaningful appreciation. It integrates with workplace tools (Jira, Git, Teams/Slack, LMS) to automatically detect work signals, analyze contributions, and generate personalized appreciation messages.

## 🎯 Features

- **Automatic Effort Detection** - Integrates with Jira, Git, Slack/Teams, Calendar, and LMS
- **AI-Powered Analysis** - Classifies efforts and scores impact using AI
- **Personalized Recognition** - Generates authentic appreciation messages
- **Badge System** - Awards badges based on contribution patterns
- **Weekly Digests** - Automated weekly summary of achievements
- **Employee Dashboard** - Centralized view of all recognitions and badges

## 🏗️ Architecture

### Technology Stack

- **Frontend:** Angular 17+ with Waypoint UI Components
- **Backend:** Java Spring Boot 3.2+
- **Database:** MongoDB 6.0+
- **AI Engine:** OpenAI GPT-4 / Azure OpenAI
- **Deployment:** Docker & Docker Compose

### Project Structure

```
Kudosly/
├── frontend/          # Angular application
├── backend/           # Spring Boot API
├── db/               # MongoDB scripts
├── docker-compose.yml
└── README.md
```

## 🚀 Quick Start

### Prerequisites

- Docker & Docker Compose
- Node.js 18+ (for local frontend development)
- Java 17+ & Maven (for local backend development)
- MongoDB 6.0+ (for local database)

### Running with Docker

1. Clone the repository:
```bash
git clone <repository-url>
cd Kudosly
```

2. Create environment file:
```bash
cp .env.example .env
# Edit .env and add your OPENAI_API_KEY
```

3. Start all services:
```bash
docker-compose up -d
```

4. Access the application:
- Frontend: http://localhost:4200
- Backend API: http://localhost:8080
- MongoDB: mongodb://localhost:27017

### Local Development

#### Frontend
```bash
cd frontend
npm install
npm start
```
Navigate to http://localhost:4200

#### Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
API available at http://localhost:8080

#### Database
```bash
# Start MongoDB
mongod --dbpath /path/to/data

# Initialize database
mongosh < db/init-db.js
mongosh < db/seed-data.js
```

## 📚 API Documentation

### Effort Events
- `POST /api/events` - Submit effort event from integrations

### Recognitions
- `GET /api/recognition/{employeeId}` - Get all recognitions
- `GET /api/recognition/{employeeId}/recent` - Get recent recognitions

### Badges
- `GET /api/badges` - Get all available badges
- `GET /api/badges/{employeeId}` - Get employee badges

### Weekly Digest
- `GET /api/digest/{employeeId}` - Get weekly digest
- `POST /api/digest/{employeeId}/generate` - Generate new digest

### User Feed
- `GET /api/user/{employeeId}/feed` - Get complete user feed

## 🎨 UI Components

The frontend uses Waypoint UI component library:

- `@waypoint/angular-components` - Angular-specific components
- `@waypoint/web-components` - Platform-agnostic web components
- `@waypoint/web-components-sdfx` - Advanced SDFX components
- `@waypoint/ui-framework` - Core design system

## 🔧 Configuration

### Backend Configuration

Edit `backend/src/main/resources/application.properties`:

```properties
# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/kudosly

# OpenAI
openai.api.key=${OPENAI_API_KEY}
openai.model=gpt-4

# Server
server.port=8080
```

### Frontend Configuration

Edit `frontend/src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

## 🎯 Demo Flow

1. Submit an effort event (e.g., Git commit, Jira ticket closure)
2. Backend receives webhook and stores effort
3. AI analyzes and classifies the effort
4. Recognition is automatically generated
5. Recognition appears in employee's feed
6. Badges are awarded based on patterns
7. Weekly digest summarizes all contributions

## 🧪 Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## 📦 Building for Production

### Frontend
```bash
cd frontend
npm run build
```

### Backend
```bash
cd backend
mvn clean package
```

### Docker Images
```bash
docker-compose build
```

## 🔐 Security

- API endpoints use CORS configuration
- MongoDB connections should use authentication in production
- OpenAI API keys stored in environment variables
- Never commit `.env` file to version control

## 🚀 Deployment

### Using Docker Compose
```bash
docker-compose up -d
```

### Manual Deployment
1. Build frontend and deploy to web server
2. Package backend as JAR and deploy to application server
3. Configure MongoDB with authentication
4. Set environment variables for API keys

## 📝 License

Copyright © 2025 Kudosly. All rights reserved.

## 🤝 Contributing

This is a hackathon project. For production use, consider:
- Adding authentication and authorization
- Implementing proper error handling
- Adding comprehensive test coverage
- Setting up CI/CD pipelines
- Implementing rate limiting
- Adding monitoring and logging

## 📧 Support

For questions or support, please contact the development team.

---

**Built with ❤️ for recognizing invisible efforts**
