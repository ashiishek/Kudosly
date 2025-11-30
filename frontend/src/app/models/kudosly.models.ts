export interface Employee {
  _id: string;
  name: string;
  email: string;
  team: string;
  role: string;
  joinDate: Date;
}

export interface Effort {
  _id: string;
  employeeId: string;
  source: 'jira' | 'git' | 'slack' | 'lms' | 'calendar';
  payload: any;
  effortType: string;
  impactScore: number;
  timestamp: Date;
}

export interface Recognition {
  _id: string;
  employeeId: string;
  effortId: string;
  message: string;
  badge: string;
  impactScore: number;
  timestamp: Date;
}

export interface Badge {
  id?: string;
  badgeId?: string;
  name: string;
  description: string;
  icon: string;
  category?: string;
  difficulty?: number;
  earned?: boolean;
  progress?: number;
  progressPercentage?: number;
  criteria?: string[];
  criteriaStatus?: string;
  earnedDate?: Date;
}

export interface WeeklyDigest {
  _id: string;
  employeeId: string;
  weekStart: Date;
  weekEnd: Date;
  summary: string;
  contributions: number;
  collaborationScore: number;
  impactScore: number;
  growthPercent: number;
  topRecognitions: Recognition[];
  collaborationMoments: string[];
  learningWins: string[];
  badgesEarned: Badge[];
}
