# Kudosly Database

MongoDB database configuration and initialization scripts.

## Setup

### Using MongoDB locally

1. Install MongoDB 6.0+
2. Start MongoDB:
```bash
mongod --dbpath /path/to/data
```

3. Initialize database:
```bash
mongosh < init-db.js
```

4. Seed sample data:
```bash
mongosh < seed-data.js
```

### Using Docker

See the docker-compose.yml in the root directory.

## Collections

### employees
- Employee information
- Indexes: email (unique), team

### efforts
- Raw effort events from integrations
- Indexes: employeeId + timestamp, source, timestamp

### recognitions
- Generated recognition messages
- Indexes: employeeId + timestamp, effortId

### badges
- Badge definitions and criteria
- Indexes: name (unique)

### weekly_digests
- Weekly summary reports
- Indexes: employeeId + weekStart + weekEnd

## Backup

```bash
mongodump --db kudosly --out /backup/path
```

## Restore

```bash
mongorestore --db kudosly /backup/path/kudosly
```
