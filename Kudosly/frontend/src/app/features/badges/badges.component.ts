import { Component, OnInit, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { KudoslyService } from '../../services/kudosly.service';
import { AuthService } from '../../services/auth.service';
import { Badge } from '../../models/kudosly.models';

interface BadgeWithProgress extends Badge {
  isEarned?: boolean;
  progress?: number;
  progressPercentage?: number;
  criteriaStatus?: string;
}

@Component({
  selector: 'app-badges',
  standalone: true,
  imports: [CommonModule, FormsModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './badges.component.html',
  styleUrls: ['./badges.component.scss']
})
export class BadgesComponent implements OnInit {
  badges: BadgeWithProgress[] = [];
  userBadges: Badge[] = [];
  filteredBadges: BadgeWithProgress[] = [];
  
  // Filter & sorting
  selectedCategory = 'all';
  selectedSort = 'recent';
  showDetailModal = false;
  selectedBadge: BadgeWithProgress | null = null;
  
  categories = [
    { id: 'all', label: 'All Badges' },
    { id: 'collaboration', label: 'Collaboration' },
    { id: 'technical', label: 'Technical' },
    { id: 'growth', label: 'Growth' },
    { id: 'achievement', label: 'Achievement' }
  ];

  sortOptions = [
    { id: 'recent', label: 'Recently Earned' },
    { id: 'difficulty', label: 'Difficulty (Hard→Easy)' },
    { id: 'progress', label: 'Progress (Nearly Earned)' }
  ];

  isLoading = false;
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
    }
    this.loadBadges();
  }

  loadBadges(): void {
    this.isLoading = true;
    this.errorMessage = '';

    // Load all badges
    this.kudoslyService.getBadges().subscribe({
      next: (data: Badge[]) => {
        this.badges = data as BadgeWithProgress[];
        
        // Load user's earned badges
        this.kudoslyService.getUserBadges(this.userId).subscribe({
          next: (earned: Badge[]) => {
            this.userBadges = earned;
            this.enrichBadgesWithProgress();
            this.applyFiltersAndSort();
            this.isLoading = false;
          },
          error: () => {
            this.enrichBadgesWithProgress();
            this.applyFiltersAndSort();
            this.isLoading = false;
          }
        });
      },
      error: () => {
        this.errorMessage = 'Failed to load badges. Please try again.';
        this.isLoading = false;
      }
    });
  }

  enrichBadgesWithProgress(): void {
    this.badges = this.badges.map(badge => {
      const isEarned = this.userBadges.some(ub => ub.id === badge.id);
      const enriched: BadgeWithProgress = { ...badge, isEarned };

      if (isEarned) {
        enriched.progress = 100;
        enriched.progressPercentage = 100;
        enriched.criteriaStatus = 'Earned';
      } else {
        // No progress for unearned badges (users start from zero)
        enriched.progress = 0;
        enriched.progressPercentage = 0;
        enriched.criteriaStatus = 'Not Started';
      }

      return enriched;
    });
  }

  onCategoryChange(categoryId: string): void {
    this.selectedCategory = categoryId;
    this.applyFiltersAndSort();
  }

  onSortChange(sortId: string): void {
    this.selectedSort = sortId;
    this.applyFiltersAndSort();
  }

  applyFiltersAndSort(): void {
    let filtered = [...this.badges];

    // Apply category filter
    if (this.selectedCategory !== 'all') {
      filtered = filtered.filter(b => (b.category || 'achievement').toLowerCase() === this.selectedCategory);
    }

    // Apply sorting
    switch (this.selectedSort) {
      case 'difficulty':
        filtered.sort((a, b) => {
          const aDifficulty = a.difficulty || 0;
          const bDifficulty = b.difficulty || 0;
          return bDifficulty - aDifficulty;
        });
        break;
      case 'progress':
        filtered.sort((a, b) => {
          const aProgress = (a.progressPercentage || 0);
          const bProgress = (b.progressPercentage || 0);
          // Nearly earned (high progress) first, but exclude earned badges
          if (a.isEarned) return 1;
          if (b.isEarned) return -1;
          return bProgress - aProgress;
        });
        break;
      case 'recent':
      default:
        filtered.sort((a, b) => {
          if (a.isEarned && !b.isEarned) return -1;
          if (!a.isEarned && b.isEarned) return 1;
          return 0;
        });
    }

    this.filteredBadges = filtered;
  }

  openDetail(badge: BadgeWithProgress): void {
    this.selectedBadge = badge;
    this.showDetailModal = true;
  }

  closeDetail(): void {
    this.showDetailModal = false;
    this.selectedBadge = null;
  }

  getProgressColor(progress: number): string {
    if (progress >= 90) return 'nearly-earned';
    if (progress >= 70) return 'good-progress';
    if (progress >= 50) return 'fair-progress';
    return 'starting';
  }

  getEarnedBadgeCount(): number {
    return this.badges.filter(b => b.isEarned).length;
  }

  getTotalBadgeCount(): number {
    return this.badges.length;
  }
}
