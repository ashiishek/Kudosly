import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Recognition, Badge, WeeklyDigest } from '../models/kudosly.models';

@Injectable({
  providedIn: 'root'
})
export class KudoslyService {
  private apiUrl = 'http://localhost:8080/api/v1';

  constructor(private http: HttpClient) {}

  // ===== RECOGNITIONS =====
  getRecognitions(): Observable<Recognition[]> {
    return this.http.get<Recognition[]>(`${this.apiUrl}/recognitions/feed`);
  }

  getRecognitionById(recognitionId: string): Observable<Recognition> {
    return this.http.get<Recognition>(`${this.apiUrl}/recognitions/${recognitionId}`);
  }

  getUserRecognitions(userId: string, page: number = 0, size: number = 10): Observable<Recognition[]> {
    return this.http.get<Recognition[]>(`${this.apiUrl}/recognitions/user/${userId}`, {
      params: new HttpParams().set('page', page.toString()).set('size', size.toString())
    });
  }

  getRecentRecognitions(userId: string, page: number = 0, size: number = 10): Observable<Recognition[]> {
    return this.http.get<Recognition[]>(`${this.apiUrl}/recognitions/user/${userId}/recent`, {
      params: new HttpParams().set('page', page.toString()).set('size', size.toString())
    });
  }

  // ===== BADGES =====
  getBadges(): Observable<Badge[]> {
    return this.http.get<Badge[]>(`${this.apiUrl}/badges`);
  }

  getUserBadges(userId: string): Observable<Badge[]> {
    return this.http.get<Badge[]>(`${this.apiUrl}/badges/user/${userId}`);
  }

  getBadgeProgress(badgeId: string, userId: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/badges/${badgeId}/user/${userId}`);
  }

  awardBadge(employeeId: string, badgeId: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/badges/award`, null, {
      params: new HttpParams().set('employeeId', employeeId).set('badgeId', badgeId)
    });
  }

  evaluateBadgeCriteria(userId: string): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/badges/evaluate/${userId}`, null);
  }

  // ===== WEEKLY DIGEST =====
  getWeeklyDigest(): Observable<WeeklyDigest> {
    return this.http.get<WeeklyDigest>(`${this.apiUrl}/digest`);
  }

  getLatestDigest(userId: string): Observable<WeeklyDigest> {
    return this.http.get<WeeklyDigest>(`${this.apiUrl}/digest/latest/${userId}`);
  }

  getUserDigest(userId: string, weekStart?: string, weekEnd?: string): Observable<WeeklyDigest> {
    let params = new HttpParams();
    if (weekStart) params = params.set('weekStart', weekStart);
    if (weekEnd) params = params.set('weekEnd', weekEnd);

    return this.http.get<WeeklyDigest>(`${this.apiUrl}/digest/${userId}`, { params });
  }

  generateDigest(userId: string, weekStart: string, weekEnd: string): Observable<WeeklyDigest> {
    return this.http.post<WeeklyDigest>(`${this.apiUrl}/digest/${userId}/generate`, null, {
      params: new HttpParams().set('weekStart', weekStart).set('weekEnd', weekEnd)
    });
  }

  // ===== USERS =====
  getUser(userId: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/users/${userId}`);
  }

  getAllUsers(team?: string, page: number = 0, size: number = 20): Observable<any[]> {
    let params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    if (team) params = params.set('team', team);

    return this.http.get<any[]>(`${this.apiUrl}/users`, { params });
  }

  createUser(userData: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/users`, userData);
  }

  getUserFeed(userId: string, page: number = 0, size: number = 10): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/users/${userId}/feed`, {
      params: new HttpParams().set('page', page.toString()).set('size', size.toString())
    });
  }

  getUserStats(userId: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/users/${userId}/stats`);
  }

  // ===== EFFORTS =====
  getUserEfforts(userId: string, page: number = 0, size: number = 10): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/efforts/user/${userId}`, {
      params: new HttpParams().set('page', page.toString()).set('size', size.toString())
    });
  }

  getEffortsByDateRange(startDate: string, endDate: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/efforts/date-range`, {
      params: new HttpParams().set('startDate', startDate).set('endDate', endDate)
    });
  }

  getEffortStats(userId?: string): Observable<any> {
    let params = new HttpParams();
    if (userId) params = params.set('userId', userId);

    return this.http.get<any>(`${this.apiUrl}/efforts/stats`, { params });
  }

  createTestEffort(employeeId: string, source: string = 'git'): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/efforts/test`, null, {
      params: new HttpParams().set('employeeId', employeeId).set('source', source)
    });
  }
}
