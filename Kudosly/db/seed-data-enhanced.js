// Seed data for Kudosly database - Fully Enhanced with comprehensive field coverage
// Run with: mongosh kudosly < seed-data-enhanced.js

// Clear existing data
db.employees.deleteMany({});
db.badges.deleteMany({});
db.efforts.deleteMany({});
db.recognitions.deleteMany({});
db.weekly_digests.deleteMany({});
db.employee_badges.deleteMany({});

console.log("Cleared existing data...");

// Insert employees
const employeeResult = db.employees.insertMany([
  {
    _id: ObjectId("507f1f77bcf86cd799439001"),
    name: "John Doe",
    email: "john.doe@company.com",
    githubUsername: "johndoe",
    slackId: "U001",
    team: "Engineering",
    role: "Senior Software Engineer",
    joinDate: new Date("2022-01-15"),
    isActive: true,
    department: "Engineering",
    manager: "Sarah Williams",
    avatar: "https://api.example.com/avatars/johndoe.jpg",
    bio: "Experienced full-stack engineer passionate about clean code",
    skills: ["Java", "TypeScript", "MongoDB", "Spring Boot", "Angular"],
    recognitionCount: 15,
    badgeCount: 3,
    totalEffortScore: 125,
    lastActivityDate: new Date()
  },
  {
    _id: ObjectId("507f1f77bcf86cd799439002"),
    name: "Jane Smith",
    email: "jane.smith@company.com",
    githubUsername: "janesmith",
    slackId: "U002",
    team: "Engineering",
    role: "Software Engineer",
    joinDate: new Date("2023-03-20"),
    isActive: true,
    department: "Engineering",
    manager: "Sarah Williams",
    avatar: "https://api.example.com/avatars/janesmith.jpg",
    bio: "Frontend specialist with eye for design",
    skills: ["TypeScript", "React", "CSS", "Angular", "Jest"],
    recognitionCount: 8,
    badgeCount: 1,
    totalEffortScore: 52,
    lastActivityDate: new Date(Date.now() - 86400000)
  },
  {
    _id: ObjectId("507f1f77bcf86cd799439003"),
    name: "Mike Johnson",
    email: "mike.johnson@company.com",
    githubUsername: "mikejohnson",
    slackId: "U003",
    team: "Product",
    role: "Product Manager",
    joinDate: new Date("2021-08-10"),
    isActive: true,
    department: "Product Management",
    manager: "CEO",
    avatar: "https://api.example.com/avatars/mikejohnson.jpg",
    bio: "Strategic thinker focused on customer value",
    skills: ["Product Strategy", "Data Analysis", "Customer Research", "Roadmapping"],
    recognitionCount: 10,
    badgeCount: 2,
    totalEffortScore: 78,
    lastActivityDate: new Date(Date.now() - 172800000)
  },
  {
    _id: ObjectId("507f1f77bcf86cd799439004"),
    name: "Sarah Williams",
    email: "sarah.williams@company.com",
    githubUsername: "sarahwilliams",
    slackId: "U004",
    team: "Engineering",
    role: "Tech Lead",
    joinDate: new Date("2020-05-01"),
    isActive: true,
    department: "Engineering",
    manager: "VP Engineering",
    avatar: "https://api.example.com/avatars/sarahwilliams.jpg",
    bio: "Engineering leader passionate about team development",
    skills: ["Java", "Architecture", "Leadership", "Mentoring", "System Design"],
    recognitionCount: 22,
    badgeCount: 5,
    totalEffortScore: 188,
    lastActivityDate: new Date()
  },
  {
    _id: ObjectId("507f1f77bcf86cd799439005"),
    name: "Alex Chen",
    email: "alex.chen@company.com",
    githubUsername: "alexchen",
    slackId: "U005",
    team: "Engineering",
    role: "Junior Software Engineer",
    joinDate: new Date("2023-09-01"),
    isActive: true,
    department: "Engineering",
    manager: "John Doe",
    avatar: "https://api.example.com/avatars/alexchen.jpg",
    bio: "Eager learner excited about backend development",
    skills: ["Java", "JavaScript", "Git", "Docker", "Testing"],
    recognitionCount: 5,
    badgeCount: 0,
    totalEffortScore: 28,
    lastActivityDate: new Date(Date.now() - 259200000)
  },
  {
    _id: ObjectId("507f1f77bcf86cd799439006"),
    name: "Emma Davis",
    email: "emma.davis@company.com",
    githubUsername: "emmadavis",
    slackId: "U006",
    team: "Design",
    role: "UI/UX Designer",
    joinDate: new Date("2022-06-15"),
    isActive: true,
    department: "Design",
    manager: "VP Design",
    avatar: "https://api.example.com/avatars/emmadavis.jpg",
    bio: "Creative designer focused on user experience",
    skills: ["Figma", "UI Design", "UX Research", "Prototyping", "CSS"],
    recognitionCount: 12,
    badgeCount: 2,
    totalEffortScore: 85,
    lastActivityDate: new Date(Date.now() - 172800000)
  }
]);

const employees = employeeResult.insertedIds;
console.log("✓ Inserted " + employees.length + " employees");

// Insert badge definitions
db.badges.insertMany([
  {
    _id: ObjectId("607f1f77bcf86cd799439001"),
    badgeId: "collaboration-hero",
    name: "Collaboration Hero",
    description: "Consistently helps teammates and contributes to cross-team initiatives",
    icon: "🤝",
    rarity: "silver",
    points: 500,
    color: "#C0C0C0",
    category: "teamwork",
    unlockCondition: "10+ collaboration efforts in 30 days",
    requirements: {
      minCollaborationEfforts: 10,
      timeWindow: "month",
      minEffortScore: 6
    },
    createdAt: new Date("2024-01-01"),
    updatedAt: new Date(),
    isActive: true,
    displayOrder: 1
  },
  {
    _id: ObjectId("607f1f77bcf86cd799439002"),
    badgeId: "problem-solver",
    name: "Problem Solver",
    description: "Resolves complex technical challenges with innovative solutions",
    icon: "🔧",
    rarity: "gold",
    points: 1000,
    color: "#FFD700",
    category: "technical",
    unlockCondition: "5+ bug fixes with score 8+",
    requirements: {
      minBugFixes: 5,
      minImpactScore: 8,
      timeWindow: "month"
    },
    createdAt: new Date("2024-01-01"),
    updatedAt: new Date(),
    isActive: true,
    displayOrder: 2
  },
  {
    _id: ObjectId("607f1f77bcf86cd799439003"),
    badgeId: "knowledge-sharer",
    name: "Knowledge Sharer",
    description: "Actively shares knowledge through documentation and mentoring",
    icon: "📚",
    rarity: "silver",
    points: 750,
    color: "#C0C0C0",
    category: "learning",
    unlockCondition: "5 mentoring + 10 code reviews",
    requirements: {
      minMentoringEfforts: 5,
      minCodeReviews: 10,
      timeWindow: "quarter"
    },
    createdAt: new Date("2024-01-01"),
    updatedAt: new Date(),
    isActive: true,
    displayOrder: 3
  },
  {
    _id: ObjectId("607f1f77bcf86cd799439004"),
    badgeId: "consistency-champion",
    name: "Consistency Champion",
    description: "Maintains high-quality contributions over extended periods",
    icon: "🎯",
    rarity: "bronze",
    points: 300,
    color: "#CD7F32",
    category: "consistency",
    unlockCondition: "30+ day contribution streak",
    requirements: {
      minStreakDays: 30,
      minDailyEfforts: 3,
      minAverageScore: 6
    },
    createdAt: new Date("2024-01-01"),
    updatedAt: new Date(),
    isActive: true,
    displayOrder: 4
  },
  {
    _id: ObjectId("607f1f77bcf86cd799439005"),
    badgeId: "innovation-spark",
    name: "Innovation Spark",
    description: "Introduces creative ideas that improve products or processes",
    icon: "💡",
    rarity: "platinum",
    points: 2000,
    color: "#E5E4E2",
    category: "innovation",
    unlockCondition: "3+ features with score 9+",
    requirements: {
      minInnovativeFeatures: 3,
      minImpactScore: 9,
      timeWindow: "quarter"
    },
    createdAt: new Date("2024-01-01"),
    updatedAt: new Date(),
    isActive: true,
    displayOrder: 5
  },
  {
    _id: ObjectId("607f1f77bcf86cd799439006"),
    badgeId: "team-player",
    name: "Team Player",
    description: "Exceptional teamwork and positive impact on team morale",
    icon: "🚀",
    rarity: "gold",
    points: 1500,
    color: "#FFD700",
    category: "teamwork",
    unlockCondition: "20+ team efforts with positive feedback",
    requirements: {
      minTeamEfforts: 20,
      positiveCollaborationRatio: 0.8,
      timeWindow: "quarter"
    },
    createdAt: new Date("2024-01-01"),
    updatedAt: new Date(),
    isActive: true,
    displayOrder: 6
  }
]);

console.log("✓ Inserted 6 badge definitions");

// Insert efforts with ENHANCED data including ALL fields
const now = new Date();
const effortsList = [];

// Efforts for John Doe
effortsList.push(
  { employeeId: "507f1f77bcf86cd799439001", source: "github", effortType: "feature-work", impactScore: 9, daysAgo: 0, title: "Implement OAuth2 authentication", description: "Comprehensive OAuth2 implementation with Google, GitHub, and Microsoft provider integrations. Enhanced security with PKCE flow and state validation." },
  { employeeId: "507f1f77bcf86cd799439001", source: "jira", effortType: "bug-fix", impactScore: 8, daysAgo: 1, title: "Fix critical memory leak", description: "Identified and fixed memory leak in connection pooling. Root cause was improper resource cleanup in exception handlers." },
  { employeeId: "507f1f77bcf86cd799439001", source: "slack", effortType: "collaboration", impactScore: 7, daysAgo: 2, title: "Helped debug production issue", description: "Assisted team with debugging production outage. Provided real-time support and helped implement temporary fix." },
  { employeeId: "507f1f77bcf86cd799439001", source: "github", effortType: "code-review", impactScore: 7, daysAgo: 3, title: "Reviewed API design PR", description: "Thorough code review of new API endpoints. Provided detailed feedback on RESTful design principles and error handling." },
  { employeeId: "507f1f77bcf86cd799439001", source: "slack", effortType: "mentoring", impactScore: 8, daysAgo: 4, title: "Mentored junior dev on testing", description: "One-on-one mentoring session covering unit testing, integration testing, and test coverage best practices." }
);

// Efforts for Jane Smith
effortsList.push(
  { employeeId: "507f1f77bcf86cd799439002", source: "github", effortType: "feature-work", impactScore: 7, daysAgo: 1, title: "Add user profile feature", description: "Implemented user profile component with edit capabilities, avatar upload, and bio management." },
  { employeeId: "507f1f77bcf86cd799439002", source: "jira", effortType: "bug-fix", impactScore: 6, daysAgo: 2, title: "Fix form validation bug", description: "Fixed form validation issues where error messages weren't displaying properly." },
  { employeeId: "507f1f77bcf86cd799439002", source: "slack", effortType: "learning", impactScore: 6, daysAgo: 3, title: "Completed TypeScript course", description: "Completed advanced TypeScript course covering generics, decorators, and advanced type system features." }
);

// Efforts for Mike Johnson
effortsList.push(
  { employeeId: "507f1f77bcf86cd799439003", source: "jira", effortType: "feature-work", impactScore: 8, daysAgo: 0, title: "Define Q4 product roadmap", description: "Created comprehensive Q4 product roadmap with prioritized features based on customer feedback and market analysis." },
  { employeeId: "507f1f77bcf86cd799439003", source: "slack", effortType: "collaboration", impactScore: 8, daysAgo: 2, title: "Coordinated cross-team initiative", description: "Led cross-functional meeting involving product, engineering, and design teams to align on new initiative." }
);

// Efforts for Sarah Williams
effortsList.push(
  { employeeId: "507f1f77bcf86cd799439004", source: "github", effortType: "feature-work", impactScore: 9, daysAgo: 0, title: "Design system implementation", description: "Built comprehensive design system with component library, typography scale, and color palette guidelines." },
  { employeeId: "507f1f77bcf86cd799439004", source: "github", effortType: "code-review", impactScore: 8, daysAgo: 1, title: "Reviewed architecture proposal", description: "Thorough review of proposed microservices architecture. Provided feedback on scaling and deployment strategy." },
  { employeeId: "507f1f77bcf86cd799439004", source: "slack", effortType: "mentoring", impactScore: 9, daysAgo: 2, title: "Led tech talk on microservices", description: "Delivered technical presentation on microservices patterns, covering service discovery, API gateways, and distributed tracing." }
);

// Efforts for Alex Chen
effortsList.push(
  { employeeId: "507f1f77bcf86cd799439005", source: "github", effortType: "feature-work", impactScore: 6, daysAgo: 1, title: "Add logging module", description: "Implemented structured logging module with multiple log levels and output formats." },
  { employeeId: "507f1f77bcf86cd799439005", source: "jira", effortType: "bug-fix", impactScore: 5, daysAgo: 2, title: "Fix CSS alignment issue", description: "Fixed flexbox alignment issues in responsive layout causing misalignment on smaller screens." }
);

// Efforts for Emma Davis
effortsList.push(
  { employeeId: "507f1f77bcf86cd799439006", source: "slack", effortType: "feature-work", impactScore: 7, daysAgo: 1, title: "Design new dashboard mockups", description: "Created high-fidelity mockups for new dashboard with improved data visualization and user metrics." },
  { employeeId: "507f1f77bcf86cd799439006", source: "slack", effortType: "collaboration", impactScore: 7, daysAgo: 2, title: "Collaborated on design system", description: "Worked with product team to develop cohesive design system ensuring consistency across all products." }
);

// Map efforts to have FULL payload with all fields
const effortsToInsert = effortsList.map((e, idx) => {
  const timestamp = new Date(now - e.daysAgo * 86400000);
  return {
    _id: ObjectId(),
    employeeId: e.employeeId,
    source: e.source,
    effortType: e.effortType,
    impactScore: e.impactScore,
    timestamp: timestamp,
    createdAt: timestamp,
    updatedAt: timestamp,
    payload: {
      title: e.title,
      description: e.description,
      source: e.source,
      action: "completed",
      url: `https://github.com/kudosly/repo/pull/${1000 + idx}`,
      tags: [e.effortType, "reviewed", "production"]
    },
    category: e.effortType,
    status: "completed",
    isPublic: true,
    comments: [
      { author: "Team Lead", text: "Great work on this!", timestamp: new Date(timestamp.getTime() + 3600000) }
    ],
    likes: Math.floor(Math.random() * 10) + 5,
    shares: Math.floor(Math.random() * 5) + 1
  };
});

db.efforts.insertMany(effortsToInsert);
console.log("✓ Inserted " + effortsToInsert.length + " efforts");

// Insert recognitions based on efforts
const recognitionsToInsert = [];
effortsToInsert.forEach((effort, idx) => {
  const templates = {
    "feature-work": "Fantastic work on {title}! Outstanding delivery and architecture. Your implementation quality is exceptional. 🚀",
    "bug-fix": "Excellent debugging on {title}! Root cause analysis was spot-on and solution is elegant. 🔧",
    "collaboration": "Amazing teamwork on {title}! Your contribution made all the difference. 🤝",
    "code-review": "Insightful code review on {title}! Your feedback improves our standards. 👀",
    "mentoring": "Outstanding mentoring! Your guidance on {title} helps the team grow. 📚",
    "learning": "Congratulations on {title}! Your growth mindset is inspiring. 🎓"
  };

  const template = templates[effort.effortType] || "Great work on {title}!";
  const message = template.replace("{title}", effort.payload.title);

  recognitionsToInsert.push({
    _id: ObjectId(),
    employeeId: effort.employeeId,
    effortId: effort._id,
    message: message,
    badge: {
      "feature-work": "🚀",
      "bug-fix": "🔧",
      "collaboration": "🤝",
      "code-review": "👀",
      "mentoring": "📚",
      "learning": "🎓"
    }[effort.effortType] || "⭐",
    impactScore: effort.impactScore,
    category: effort.effortType,
    timestamp: effort.timestamp,
    createdAt: effort.timestamp,
    updatedAt: effort.timestamp,
    likes: Math.floor(Math.random() * 8) + 2,
    shares: Math.floor(Math.random() * 3) + 1
  });
});

db.recognitions.insertMany(recognitionsToInsert);
console.log("✓ Inserted " + recognitionsToInsert.length + " recognitions");

// Insert employee badges (earned) with FULL fields
const employeeBadgesToInsert = [
  { 
    _id: ObjectId(), 
    employeeId: "507f1f77bcf86cd799439001", 
    badgeId: "problem-solver", 
    earnedDate: new Date(now - 10 * 86400000), 
    progressPercentage: 100,
    createdAt: new Date(now - 10 * 86400000),
    updatedAt: new Date(now - 10 * 86400000),
    unlockedAt: new Date(now - 10 * 86400000)
  },
  { 
    _id: ObjectId(), 
    employeeId: "507f1f77bcf86cd799439001", 
    badgeId: "knowledge-sharer", 
    earnedDate: new Date(now - 5 * 86400000), 
    progressPercentage: 100,
    createdAt: new Date(now - 5 * 86400000),
    updatedAt: new Date(now - 5 * 86400000),
    unlockedAt: new Date(now - 5 * 86400000)
  },
  { 
    _id: ObjectId(), 
    employeeId: "507f1f77bcf86cd799439004", 
    badgeId: "innovation-spark", 
    earnedDate: new Date(now - 15 * 86400000), 
    progressPercentage: 100,
    createdAt: new Date(now - 15 * 86400000),
    updatedAt: new Date(now - 15 * 86400000),
    unlockedAt: new Date(now - 15 * 86400000)
  },
  { 
    _id: ObjectId(), 
    employeeId: "507f1f77bcf86cd799439004", 
    badgeId: "team-player", 
    earnedDate: new Date(now - 20 * 86400000), 
    progressPercentage: 100,
    createdAt: new Date(now - 20 * 86400000),
    updatedAt: new Date(now - 20 * 86400000),
    unlockedAt: new Date(now - 20 * 86400000)
  },
  { 
    _id: ObjectId(), 
    employeeId: "507f1f77bcf86cd799439002", 
    badgeId: "consistency-champion", 
    earnedDate: new Date(now - 8 * 86400000), 
    progressPercentage: 100,
    createdAt: new Date(now - 8 * 86400000),
    updatedAt: new Date(now - 8 * 86400000),
    unlockedAt: new Date(now - 8 * 86400000)
  },
  { 
    _id: ObjectId(), 
    employeeId: "507f1f77bcf86cd799439003", 
    badgeId: "collaboration-hero", 
    earnedDate: new Date(now - 12 * 86400000), 
    progressPercentage: 100,
    createdAt: new Date(now - 12 * 86400000),
    updatedAt: new Date(now - 12 * 86400000),
    unlockedAt: new Date(now - 12 * 86400000)
  }
];

db.employee_badges.insertMany(employeeBadgesToInsert);
console.log("✓ Inserted " + employeeBadgesToInsert.length + " employee badges");

// Insert weekly digests with FULL field coverage
const employeeIds = [
  ObjectId("507f1f77bcf86cd799439001"),
  ObjectId("507f1f77bcf86cd799439002"),
  ObjectId("507f1f77bcf86cd799439003"),
  ObjectId("507f1f77bcf86cd799439004"),
  ObjectId("507f1f77bcf86cd799439005"),
  ObjectId("507f1f77bcf86cd799439006")
];

const weeklyDigests = [];
for (let i = 0; i < employeeIds.length; i++) {
  const weekStart = new Date(now - (7 - i % 4) * 86400000);
  weekStart.setHours(0, 0, 0, 0);
  const weekEnd = new Date(weekStart.getTime() + 7 * 86400000);
  
  weeklyDigests.push({
    _id: ObjectId(),
    employeeId: employeeIds[i],
    weekStart: weekStart,
    weekEnd: weekEnd,
    weekStartDate: weekStart,
    weekEndDate: weekEnd,
    summary: "Great week! You completed " + (3 + Math.floor(Math.random() * 5)) + " efforts with high impact.",
    narrative: "This week showcased excellent contributions across multiple domains. Your efforts on feature development and collaboration drove team momentum forward. The quality of your work continues to inspire the team and sets a strong example for continuous improvement.",
    topRecognitions: recognitionsToInsert.slice(i * 2, i * 2 + 2).map(r => r._id),
    collaborationScore: 0.75 + Math.random() * 0.25,
    learningWins: [
      "Mastered new technology",
      "Helped team grow",
      "Improved code quality practices"
    ],
    badgesEarned: employeeBadgesToInsert.slice(i, i + 1).map(b => b.badgeId),
    highlights: [
      "Led 2 major feature developments",
      "Resolved critical bugs",
      "Mentored junior team members",
      "Improved system performance by 15%"
    ],
    metrics: {
      totalEfforts: 5 + Math.floor(Math.random() * 5),
      averageImpactScore: 7.0 + Math.random() * 2.5,
      recognitionRate: 0.8 + Math.random() * 0.2,
      collaborationScore: 0.75 + Math.random() * 0.25,
      bugFixRate: 0.6 + Math.random() * 0.3,
      codeReviewsCompleted: 8 + Math.floor(Math.random() * 5),
      mentoringHours: Math.floor(Math.random() * 10) + 5
    },
    topContributors: ["John Doe", "Sarah Williams", "Jane Smith"],
    totalEfforts: 8 + Math.floor(Math.random() * 4),
    totalRecognitions: 6 + Math.floor(Math.random() * 3),
    createdAt: new Date(),
    updatedAt: new Date(),
    isPublished: true
  });
}

if (weeklyDigests.length > 0) {
  db.weekly_digests.insertMany(weeklyDigests);
  console.log("✓ Inserted " + weeklyDigests.length + " weekly digests");
} else {
  console.log("⚠ No weekly digests to insert");
}

// Summary
console.log("\n" + "=".repeat(50));
console.log("✅ Enhanced Mock Data Seeding Complete!");
console.log("=".repeat(50));
console.log("Employees:       " + db.employees.countDocuments());
console.log("Badges:          " + db.badges.countDocuments());
console.log("Efforts:         " + db.efforts.countDocuments());
console.log("Recognitions:    " + db.recognitions.countDocuments());
console.log("Employee Badges: " + db.employee_badges.countDocuments());
console.log("Weekly Digests:  " + db.weekly_digests.countDocuments());
console.log("=".repeat(50));
console.log("\n📊 Enhanced Field Coverage:");
console.log("  ✓ Employees: All 15 fields populated");
console.log("  ✓ Badges: All 10 fields populated");
console.log("  ✓ Efforts: All 9 fields (createdAt, updatedAt, status, isPublic, comments, likes, shares, payload, category)");
console.log("  ✓ Recognitions: Extended with timestamps and engagement metrics");
console.log("  ✓ Employee Badges: Complete with unlock timestamps");
console.log("  ✓ Weekly Digests: Comprehensive with metrics, highlights, and contributors");
console.log("=".repeat(50));
