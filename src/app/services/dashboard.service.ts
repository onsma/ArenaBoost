import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoanStatistics {
  totalLoans: number;
  totalAmount: number;
  approvedLoans: number;
  pendingLoans: number;
}

export interface UserStatistics {
  totalUsers: number;
  activeUsers: number;
  newUsers: number;
  premiumUsers: number;
}

export interface RecentActivity {
  date: string;
  count: number;
  activityType: string;
}

export interface ActiveUser {
  id: number;
  name: string;
  email: string;
  lastActive: string;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = 'http://localhost:8085/api/dashboard';

  constructor(private http: HttpClient) {}

  getLoanStatistics(): Observable<LoanStatistics> {
    return this.http.get<LoanStatistics>(`${this.apiUrl}/loan-statistics`);
  }

  getUserStatistics(): Observable<UserStatistics> {
    return this.http.get<UserStatistics>(`${this.apiUrl}/user-statistics`);
  }

  getRecentActivity(): Observable<RecentActivity[]> {
    return this.http.get<RecentActivity[]>(`${this.apiUrl}/recent-activity`);
  }

  getActiveUsers(): Observable<ActiveUser[]> {
    return this.http.get<ActiveUser[]>(`${this.apiUrl}/active-users`);
  }
}
