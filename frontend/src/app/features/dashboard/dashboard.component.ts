import { Component, OnInit, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { KudoslyService } from '../../services/kudosly.service';
import { AuthService } from '../../services/auth.service';
import { Recognition } from '../../models/kudosly.models';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  userName = '';
  userTeam = '';
  totalRecognitions = 0;
  totalBadges = 0;
  impactScore = 0;
  streakDays = 15;
  recentRecognitions: Recognition[] = [];
  isLoading = true;
  errorMessage = '';

  private userId = '';

  constructor(
    private kudoslyService: KudoslyService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      this.userId = currentUser.id;
      this.userName = currentUser.name;
      this.userTeam = currentUser.team || 'Team';
      this.loadDashboardData();
    }
  }

  loadDashboardData(): void {
    this.isLoading = true;
    this.errorMessage = '';

    forkJoin({
      user: this.kudoslyService.getUser(this.userId),
      recognitions: this.kudoslyService.getUserRecognitions(this.userId, 0, 5),
      badges: this.kudoslyService.getUserBadges(this.userId),
      stats: this.kudoslyService.getUserStats(this.userId)
    }).subscribe({
      next: (data) => {
        // User info
        if (data.user) {
          this.userName = data.user.name || this.userName;
          this.userTeam = data.user.team || this.userTeam;
        }

        // Recognitions
        this.recentRecognitions = data.recognitions?.slice(0, 5) || [];
        this.totalRecognitions = data.stats?.totalRecognitions || data.recognitions?.length || 0;
        
        // Badges
        this.totalBadges = data.badges?.length || 0;

        // Impact score from stats
        if (data.stats?.averageImpactScore) {
          this.impactScore = Math.round(data.stats.averageImpactScore * 10);
        } else if (data.recognitions && data.recognitions.length > 0) {
          const avgScore = data.recognitions.reduce((sum, r) => sum + (r.impactScore || 0), 0) / data.recognitions.length;
          this.impactScore = Math.round(avgScore * 10);
        }
        
        this.isLoading = false;
      },
      error: (err: any) => {
        console.error('Error loading dashboard data:', err);
        this.errorMessage = 'Failed to load dashboard data';
        this.isLoading = false;
      }
    });
  }
}
