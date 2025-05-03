import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Loan } from './models/loan.model';

@Injectable({
  providedIn: 'root'
})
export class LoanService {
  private apiUrl = 'http://localhost:8085/api/loans';

  constructor(private http: HttpClient) {}

  // Créer une nouvelle demande de prêt
  createLoan(loan: Loan): Observable<Loan> {
    const body = {
      amount: loan.amount,
      loantype: loan.loantype.name,
      interest_rate: loan.interest_rate,
      refund_duration: loan.refund_duration
    };
    return this.http.post<Loan>(`${this.apiUrl}/${loan.user.id_user}`, body);
  }

  // Récupérer un prêt par ID
  getLoan(id: number): Observable<Loan> {
    return this.http.get<Loan>(`${this.apiUrl}/${id}`);
  }

  // Récupérer tous les prêts
  getAllLoans(): Observable<Loan[]> {
    return this.http.get<Loan[]>(`${this.apiUrl}`);
  }

  // Mettre à jour un prêt
  updateLoan(id: number, loan: Loan): Observable<Loan> {
    const body = {
      amount: loan.amount,
      loantype: loan.loantype.name,
      interest_rate: loan.interest_rate,
      refund_duration: loan.refund_duration
    };
    return this.http.put<Loan>(`${this.apiUrl}/${id}`, body);
  }

  // Supprimer un prêt
  deleteLoan(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Obtenir les statistiques des prêts
  getLoanStatistics(): Observable<any> {
    return this.http.get(`${this.apiUrl}/statistics`);
  }

  // Rechercher des prêts
  searchLoans(criteria: any): Observable<Loan[]> {
    return this.http.post<Loan[]>(`${this.apiUrl}/search`, criteria);
  }

  // Télécharger l'historique des prêts en PDF
  downloadLoanHistory(userId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${userId}/pdf`, { responseType: 'blob' });
  }
}
