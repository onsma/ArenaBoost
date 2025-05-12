import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Risk } from '../models/risk';

@Injectable({
  providedIn: 'root',
})
export class RiskService {
  private apiUrl = 'http://localhost:8081/arenaboost/risk'; // Update with your backend URL

  constructor(private http: HttpClient) {}

  getAllRisks(): Observable<Risk[]> {
    return this.http.get<Risk[]>(`${this.apiUrl}/getRisk  `);
  }

  addRisk(risk: Risk): Observable<Risk> {
    return this.http.post<Risk>(`${this.apiUrl}/AddRisk`, risk);
  }

  updateRisk(id: number, risk: Risk): Observable<Risk> {
    return this.http.put<Risk>(`${this.apiUrl}/updateRisk/${id}`, risk);
  }

  deleteRisk(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/deleteRisk/${id}`);
  }

  calculateRiskScore(id: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/calculateScore/${id}`);
  }

  checkRiskAndNotify(id: number): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/checkRisk/${id}`, {});
  }

  generateReport(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/generateReport`, { responseType: 'blob' });
  }
}