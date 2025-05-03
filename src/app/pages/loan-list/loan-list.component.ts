import { Component, OnInit } from '@angular/core';
import { LoanService } from '../../services/loan.service';
import { Loan } from '../../services/models/loan.model';

@Component({
  selector: 'app-loan-list',
  templateUrl: './loan-list.component.html',
  styleUrls: ['./loan-list.component.scss']
})
export class LoanListComponent implements OnInit {
  loans: Loan[] = [];
  loading: boolean = true;
  error: string | null = null;

  // Pagination properties
  currentPage: number = 1;
  pageSize: number = 10;
  totalPages: number = 1;
  paginatedLoans: Loan[] = [];

  constructor(private loanService: LoanService) { }

  ngOnInit(): void {
    this.loadLoans();
  }

  loadLoans(): void {
    this.loading = true;
    this.error = null;

    this.loanService.getAllLoans().subscribe({
      next: (data) => {
        this.loans = data;
        this.calculateTotalPages();
        this.goToPage(1);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading loans:', err);
        this.error = 'Une erreur est survenue lors du chargement des prêts. Veuillez réessayer plus tard.';
        this.loading = false;
      }
    });
  }

  /**
   * Calcule le nombre total de pages en fonction du nombre de prêts et de la taille de page
   */
  calculateTotalPages(): void {
    this.totalPages = Math.ceil(this.loans.length / this.pageSize);
    if (this.totalPages === 0) this.totalPages = 1;
  }

  /**
   * Navigue vers la page spécifiée
   * @param page Numéro de page
   */
  goToPage(page: number): void {
    if (page < 1) page = 1;
    if (page > this.totalPages) page = this.totalPages;

    this.currentPage = page;
    const startIndex = (page - 1) * this.pageSize;
    const endIndex = Math.min(startIndex + this.pageSize, this.loans.length);
    this.paginatedLoans = this.loans.slice(startIndex, endIndex);
  }

  /**
   * Navigue vers la page précédente
   */
  previousPage(): void {
    if (this.currentPage > 1) {
      this.goToPage(this.currentPage - 1);
    }
  }

  /**
   * Navigue vers la page suivante
   */
  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.goToPage(this.currentPage + 1);
    }
  }

  /**
   * Génère un tableau de numéros de page à afficher dans la pagination
   * @returns Tableau de numéros de page
   */
  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxPagesToShow = 5;

    if (this.totalPages <= maxPagesToShow) {
      // Si le nombre total de pages est inférieur ou égal au nombre maximum de pages à afficher,
      // afficher toutes les pages
      for (let i = 1; i <= this.totalPages; i++) {
        pages.push(i);
      }
    } else {
      // Sinon, afficher un sous-ensemble de pages autour de la page courante
      let startPage = Math.max(1, this.currentPage - Math.floor(maxPagesToShow / 2));
      let endPage = startPage + maxPagesToShow - 1;

      if (endPage > this.totalPages) {
        endPage = this.totalPages;
        startPage = Math.max(1, endPage - maxPagesToShow + 1);
      }

      for (let i = startPage; i <= endPage; i++) {
        pages.push(i);
      }
    }

    return pages;
  }

  getStatusLabel(status: string): string {
    const statusMap: { [key: string]: string } = {
      'PENDING': 'En attente',
      'APPROVED': 'Approuvé',
      'REJECTED': 'Rejeté',
      'PAID': 'Payé'
    };
    return statusMap[status] || status;
  }

  getStatusClass(status: string): string {
    const statusClassMap: { [key: string]: string } = {
      'PENDING': 'status-pending',
      'APPROVED': 'status-approved',
      'REJECTED': 'status-rejected',
      'PAID': 'status-paid'
    };
    return statusClassMap[status] || '';
  }
}
