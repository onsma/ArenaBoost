import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoanSimulationRequest {
  amount: number;
  duration: number;
  interestRate: number;
}

export interface LoanSimulationResponse {
  monthlyPayment: number;
  totalInterest: number;
  totalAmount: number;
}

@Injectable({
  providedIn: 'root'
})
export class LoanSimulationService {
  private apiUrl = 'http://localhost:8085/api/loans';

  constructor(private http: HttpClient) { }

  simulateLoan(request: LoanSimulationRequest): Observable<LoanSimulationResponse> {
    return this.http.post<LoanSimulationResponse>(`${this.apiUrl}/simulate`, request);
  }
}
