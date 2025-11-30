import { Component, OnInit, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { KudoslyService } from '../../services/kudosly.service';
import { AuthService } from '../../services/auth.service';
import { Recognition } from '../../models/kudosly.models';

@Component({
  selector: 'app-recognitions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './recognitions.component.html',
  styleUrls: ['./recognitions.component.scss']
})
export class RecognitionsComponent implements OnInit {
  recognitions: Recognition[] = [];
  filteredRecognitions: Recognition[] = [];
  selectedFilter = 'all';
  currentPage = 0;
  pageSize = 10;
  totalRecognitions = 0;
  isLoading = true;
  errorMessage = '';
  selectedRecognition: Recognition | null = null;
  showDetailModal = false;

  private userId = '';

  filters = [
    { id: 'all', label: 'All' },
    { id: 'collaboration', label: 'Collaboration' },
    { id: 'problem-solving', label: 'Problem Solving' },
    { id: 'learning', label: 'Learning' },
    { id: 'bug-fix', label: 'Bug Fixes' },
    { id: 'feature', label: 'Features' }
  ];

  sortOptions = [
    { id: 'recent', label: 'Most Recent' },
    { id: 'impact-high', label: 'Highest Impact' },
    { id: 'impact-low', label: 'Lowest Impact' }
  ];

  selectedSort = 'recent';

  constructor(
    private kudoslyService: KudoslyService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      this.userId = currentUser.id;
    }
    this.loadRecognitions();
  }

  loadRecognitions(): void {
    this.isLoading = true;
    this.errorMessage = '';

    // Load user-specific recognitions
    this.kudoslyService.getUserRecognitions(this.userId, this.currentPage, this.pageSize).subscribe({
      next: (data: Recognition[]) => {
        this.recognitions = data || [];
        this.totalRecognitions = this.recognitions.length;
        this.filterAndSort();
        this.isLoading = false;
      },
      error: (err: any) => {
        console.error('Error loading recognitions:', err);
        this.errorMessage = 'Failed to load recognitions';
        this.isLoading = false;
      }
    });
  }

  filterAndSort(): void {
    let filtered = this.recognitions;

    // Filter
    if (this.selectedFilter !== 'all') {
      filtered = filtered.filter(r =>
        (r.badge && r.badge.toLowerCase().includes(this.selectedFilter.toLowerCase())) ||
        (r.message && r.message.toLowerCase().includes(this.selectedFilter.toLowerCase()))
      );
    }

    // Sort
    if (this.selectedSort === 'impact-high') {
      filtered.sort((a, b) => (b.impactScore || 0) - (a.impactScore || 0));
    } else if (this.selectedSort === 'impact-low') {
      filtered.sort((a, b) => (a.impactScore || 0) - (b.impactScore || 0));
    }

    this.filteredRecognitions = filtered;
  }

  onFilterChange(filterId: string): void {
    this.selectedFilter = filterId;
    this.currentPage = 0;
    this.loadRecognitions();
  }

  onSortChange(sortId: string): void {
    this.selectedSort = sortId;
    this.filterAndSort();
  }

  onPreviousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadRecognitions();
    }
  }

  onNextPage(): void {
    if ((this.currentPage + 1) * this.pageSize < this.totalRecognitions) {
      this.currentPage++;
      this.loadRecognitions();
    }
  }

  openDetail(recognition: Recognition): void {
    this.selectedRecognition = recognition;
    this.showDetailModal = true;
  }

  closeDetail(): void {
    this.showDetailModal = false;
    this.selectedRecognition = null;
  }

  get hasNextPage(): boolean {
    return (this.currentPage + 1) * this.pageSize < this.totalRecognitions;
  }

  get hasPreviousPage(): boolean {
    return this.currentPage > 0;
  }

  get totalPages(): number {
    return Math.ceil(this.totalRecognitions / this.pageSize);
  }

  get currentPageStart(): number {
    return this.currentPage * this.pageSize + 1;
  }

  get currentPageEnd(): number {
    return Math.min((this.currentPage + 1) * this.pageSize, this.totalRecognitions);
  }
}
