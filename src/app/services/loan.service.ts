@Injectable({
  providedIn: 'root'
})
export class LoanService {
  private apiUrl = 'http://localhost:8080/api/loans';

  constructor(private http: HttpClient) {}

  getLoans(): Observable<any> {
    return this.http.get(this.apiUrl);
  }

  createLoan(loanData: any): Observable<any> {
    return this.http.post(this.apiUrl, loanData);
  }

  generatePDF(loanId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${loanId}/pdf`, 
      { responseType: 'blob' });
  }

  getStatistics(): Observable<any> {
    return this.http.get(`${this.apiUrl}/statistics`);
  }
}