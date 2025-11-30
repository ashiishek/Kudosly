import { Component, OnInit, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { KudoslyService } from '../../services/kudosly.service';
import { AuthService } from '../../services/auth.service';
import { WeeklyDigest, Recognition, Badge } from '../../models/kudosly.models';

interface DigestWeek {
  weekStart: Date;
  weekEnd: Date;
  label: string;
}

@Component({
  selector: 'app-digest',
  standalone: true,
  imports: [CommonModule, FormsModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './digest.component.html',
  styleUrls: ['./digest.component.scss']
})
export class DigestComponent implements OnInit {
  weeklyDigest: WeeklyDigest | null = null;
  topRecognitions: Recognition[] = [];
  earnedBadgesThisWeek: Badge[] = [];
  
  // Week navigation
  currentWeekStart: Date = new Date();
  weeks: DigestWeek[] = [];
  selectedWeekIndex = 0;
  
  // Metrics
  totalEfforts = 0;
  collaborationScore = 0;
  impactScore = 0;
  learningActivities = 0;
  
  // Loading & error states
  isLoading = false;
  errorMessage = '';
  
  private userId = '';

  constructor(
    private kudoslyService: KudoslyService,
    private authService: AuthService
  ) {
    this.initializeWeeks();
  }

  ngOnInit(): void {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      this.userId = currentUser.id;
    }
    this.loadDigest();
  }

  initializeWeeks(): void {
    const today = new Date();
    this.weeks = [];

    for (let i = 3; i >= 0; i--) {
      const startDate = new Date(today);
      startDate.setDate(today.getDate() - (today.getDay()) + (i * 7));
      const endDate = new Date(startDate);
      endDate.setDate(startDate.getDate() + 6);

      this.weeks.push({
        weekStart: startDate,
        weekEnd: endDate,
        label: `Week of ${this.formatDate(startDate)}`
      });
    }

    this.selectedWeekIndex = this.weeks.length - 1;
    this.currentWeekStart = this.weeks[this.selectedWeekIndex].weekStart;
  }

  loadDigest(): void {
    this.isLoading = true;
    this.errorMessage = '';

    const selectedWeek = this.weeks[this.selectedWeekIndex];
    const startDate = selectedWeek.weekStart.toISOString().split('T')[0];
    const endDate = selectedWeek.weekEnd.toISOString().split('T')[0];

    this.kudoslyService.getUserDigest(this.userId, startDate, endDate).subscribe({
      next: (data: WeeklyDigest) => {
        this.weeklyDigest = data;
        this.extractMetrics(data);
        this.loadRelatedData();
      },
      error: () => {
        this.errorMessage = 'Failed to load digest. Please try again.';
        this.isLoading = false;
      }
    });
  }

  loadRelatedData(): void {
    // Load top recognitions for this week
    this.kudoslyService.getRecentRecognitions(this.userId, 5).subscribe({
      next: (data: Recognition[]) => {
        this.topRecognitions = data;
      }
    });

    // Load badges earned this week
    this.kudoslyService.getUserBadges(this.userId).subscribe({
      next: (data: Badge[]) => {
        // Filter to show recent ones
        this.earnedBadgesThisWeek = data.slice(0, 3);
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  extractMetrics(digest: WeeklyDigest): void {
    this.totalEfforts = digest.contributions || 0;
    this.collaborationScore = digest.collaborationScore || 0;
    this.impactScore = digest.impactScore || 0;
    this.learningActivities = digest.learningWins?.length || 0;
  }

  onWeekChange(weekIndex: number): void {
    this.selectedWeekIndex = weekIndex;
    this.currentWeekStart = this.weeks[weekIndex].weekStart;
    this.loadDigest();
  }

  onPreviousWeek(): void {
    if (this.selectedWeekIndex > 0) {
      this.onWeekChange(this.selectedWeekIndex - 1);
    }
  }

  onNextWeek(): void {
    if (this.selectedWeekIndex < this.weeks.length - 1) {
      this.onWeekChange(this.selectedWeekIndex + 1);
    }
  }

  formatDate(date: Date): string {
    const options: Intl.DateTimeFormatOptions = { month: 'short', day: 'numeric' };
    return date.toLocaleDateString('en-US', options);
  }

  getMetricColor(score: number): string {
    if (score >= 80) return 'excellent';
    if (score >= 60) return 'good';
    if (score >= 40) return 'fair';
    return 'needs-improvement';
  }

  getMetricIcon(metric: string): string {
    switch (metric) {
      case 'collaboration': return '🤝';
      case 'impact': return '⚡';
      case 'learning': return '📚';
      case 'efforts': return '✨';
      default: return '📊';
    }
  }
}
