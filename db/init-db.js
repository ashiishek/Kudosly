// MongoDB Database Setup Script for Kudosly
// Run this script in MongoDB shell or MongoDB Compass

// Switch to kudosly database
use kudosly;

// Create collections with validation
db.createCollection("employees", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["name", "email", "team", "role"],
      properties: {
        name: {
          bsonType: "string",
          description: "Employee name is required"
        },
        email: {
          bsonType: "string",
          pattern: "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
          description: "Valid email is required"
        },
        team: {
          bsonType: "string",
          description: "Team name is required"
        },
        role: {
          bsonType: "string",
          description: "Role is required"
        },
        joinDate: {
          bsonType: "date",
          description: "Join date"
        }
      }
    }
  }
});

db.createCollection("efforts", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["employeeId", "source", "payload", "timestamp"],
      properties: {
        employeeId: {
          bsonType: "string",
          description: "Employee ID is required"
        },
        source: {
          enum: ["jira", "git", "slack", "teams", "lms", "calendar"],
          description: "Source must be one of the allowed values"
        },
        payload: {
          bsonType: "object",
          description: "Payload is required"
        },
        effortType: {
          bsonType: "string",
          description: "Type of effort"
        },
        impactScore: {
          bsonType: "int",
          minimum: 1,
          maximum: 10,
          description: "Impact score between 1-10"
        },
        timestamp: {
          bsonType: "date",
          description: "Timestamp is required"
        }
      }
    }
  }
});

db.createCollection("recognitions", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["employeeId", "effortId", "message", "timestamp"],
      properties: {
        employeeId: {
          bsonType: "string",
          description: "Employee ID is required"
        },
        effortId: {
          bsonType: "string",
          description: "Effort ID is required"
        },
        message: {
          bsonType: "string",
          description: "Recognition message is required"
        },
        badge: {
          bsonType: "string",
          description: "Badge emoji"
        },
        impactScore: {
          bsonType: "int",
          minimum: 1,
          maximum: 10
        },
        timestamp: {
          bsonType: "date",
          description: "Timestamp is required"
        }
      }
    }
  }
});

db.createCollection("badges");
db.createCollection("weekly_digests");

// Create indexes for better query performance
db.employees.createIndex({ "email": 1 }, { unique: true });
db.employees.createIndex({ "team": 1 });

db.efforts.createIndex({ "employeeId": 1, "timestamp": -1 });
db.efforts.createIndex({ "source": 1 });
db.efforts.createIndex({ "timestamp": -1 });

db.recognitions.createIndex({ "employeeId": 1, "timestamp": -1 });
db.recognitions.createIndex({ "effortId": 1 });

db.badges.createIndex({ "name": 1 }, { unique: true });

db.weekly_digests.createIndex({ "employeeId": 1, "weekStart": 1, "weekEnd": 1 });

print("✅ Collections and indexes created successfully!");
