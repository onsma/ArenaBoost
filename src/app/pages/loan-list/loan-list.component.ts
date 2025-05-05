import { Component, OnInit } from '@angular/core';
import { LoanService } from '../../services/loan.service';
import { Loan, LoanType } from '../../services/models/loan.model';
import { FormsModule } from '@angular/forms';
import { LoanTypeService } from '../../services/loantype.service';
declare var bootstrap: any; // Pour utiliser Bootstrap JS

@Component({
  selector: 'app-loan-list',
  templateUrl: './loan-list.component.html',
  styleUrls: ['./loan-list.component.scss']
})
export class LoanListComponent implements OnInit {
  loans: Loan[] = [];
  filteredLoans: Loan[] = []; // Loans after filtering
  loading: boolean = true;
  error: string | null = null;

  // Pagination properties
  currentPage: number = 1;
  pageSize: number = 10;
  totalPages: number = 1;
  paginatedLoans: Loan[] = [];

  // Modal properties
  selectedLoan: Loan | null = null;
  editModal: any;

  // Search panel properties
  isSearchPanelOpen: boolean = false;
  loanTypes: string[] = [
    'EQUIPMENT', 'TRAINING', 'MEDICAL', 'TRAVEL',
    'MARKETING', 'INFRASTRUCTURE', 'TEAM_MANAGEMENT',
    'TOURNAMENTS', 'RECRUITMENT'
  ];

  // Search criteria
  searchCriteria = {
    id: null as number | null,
    minAmount: null as number | null,
    maxAmount: null as number | null,
    loanType: '' as string,
    status: '' as string,
    minRate: null as number | null,
    maxRate: null as number | null,
    minDuration: null as number | null,
    maxDuration: null as number | null,
    startDate: null as string | null,
    endDate: null as string | null
  };

  statuses: {value: string, label: string}[] = [
    {value: 'pending', label: 'Pending'},
    {value: 'approved', label: 'Approved'},
    {value: 'rejected', label: 'Rejected'},
    {value: 'completed', label: 'Completed'}
  ];

  constructor(
    private loanService: LoanService,
    private loanTypeService: LoanTypeService
  ) { }

  ngOnInit(): void {
    this.loadLoans();

    // Initialiser le modal une fois que le DOM est chargé
    document.addEventListener('DOMContentLoaded', () => {
      const modalElement = document.getElementById('editLoanModal');
      if (modalElement) {
        this.editModal = new bootstrap.Modal(modalElement);
      }
    });
  }

  loadLoans(): void {
    this.loading = true;
    this.error = null;

    this.loanService.getAllLoans().subscribe({
      next: (data) => {
        // Log des données reçues pour comprendre leur structure
        console.log('Données reçues du backend:', data);

        this.loans = data;
        this.filteredLoans = [...this.loans]; // Initialize filtered loans with all loans
        this.calculateTotalPages();
        this.goToPage(1);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading loans:', err);
        this.error = 'An error occurred while loading loans. Please try again later.';
        this.loading = false;
      }
    });
  }

  /**
   * Toggle search panel visibility
   */
  toggleSearchPanel(): void {
    this.isSearchPanelOpen = !this.isSearchPanelOpen;
  }

  /**
   * Apply search filters to the loans list
   */
  applyFilters(): void {
    // Start with all loans
    this.filteredLoans = [...this.loans];

    // Apply each filter if it has a value
    if (this.searchCriteria.id !== null) {
      this.filteredLoans = this.filteredLoans.filter(loan =>
        loan.id_loan === this.searchCriteria.id
      );
    }

    if (this.searchCriteria.minAmount !== null) {
      this.filteredLoans = this.filteredLoans.filter(loan =>
        loan.amount >= (this.searchCriteria.minAmount || 0)
      );
    }

    if (this.searchCriteria.maxAmount !== null) {
      this.filteredLoans = this.filteredLoans.filter(loan =>
        loan.amount <= (this.searchCriteria.maxAmount || Infinity)
      );
    }

    if (this.searchCriteria.loanType) {
      this.filteredLoans = this.filteredLoans.filter(loan =>
        loan.loantype && loan.loantype.name === this.searchCriteria.loanType
      );
    }

    if (this.searchCriteria.status) {
      this.filteredLoans = this.filteredLoans.filter(loan =>
        loan.status === this.searchCriteria.status
      );
    }

    if (this.searchCriteria.minRate !== null) {
      this.filteredLoans = this.filteredLoans.filter(loan =>
        loan.interest_rate >= (this.searchCriteria.minRate || 0)
      );
    }

    if (this.searchCriteria.maxRate !== null) {
      this.filteredLoans = this.filteredLoans.filter(loan =>
        loan.interest_rate <= (this.searchCriteria.maxRate || Infinity)
      );
    }

    if (this.searchCriteria.minDuration !== null) {
      this.filteredLoans = this.filteredLoans.filter(loan =>
        loan.refund_duration >= (this.searchCriteria.minDuration || 0)
      );
    }

    if (this.searchCriteria.maxDuration !== null) {
      this.filteredLoans = this.filteredLoans.filter(loan =>
        loan.refund_duration <= (this.searchCriteria.maxDuration || Infinity)
      );
    }

    if (this.searchCriteria.startDate) {
      const startDate = new Date(this.searchCriteria.startDate);
      this.filteredLoans = this.filteredLoans.filter(loan => {
        if (!loan.requestDate) return true;
        const loanDate = new Date(loan.requestDate);
        return loanDate >= startDate;
      });
    }

    if (this.searchCriteria.endDate) {
      const endDate = new Date(this.searchCriteria.endDate);
      endDate.setHours(23, 59, 59, 999); // Set to end of day
      this.filteredLoans = this.filteredLoans.filter(loan => {
        if (!loan.requestDate) return true;
        const loanDate = new Date(loan.requestDate);
        return loanDate <= endDate;
      });
    }

    // Update pagination based on filtered results
    this.calculateTotalPages();
    this.goToPage(1); // Reset to first page after filtering
  }

  /**
   * Reset all search filters
   */
  resetFilters(): void {
    this.searchCriteria = {
      id: null,
      minAmount: null,
      maxAmount: null,
      loanType: '',
      status: '',
      minRate: null,
      maxRate: null,
      minDuration: null,
      maxDuration: null,
      startDate: null,
      endDate: null
    };

    this.filteredLoans = [...this.loans];
    this.calculateTotalPages();
    this.goToPage(1);
  }

  /**
   * Calcule le nombre total de pages en fonction du nombre de prêts filtrés et de la taille de page
   */
  calculateTotalPages(): void {
    this.totalPages = Math.ceil(this.filteredLoans.length / this.pageSize);
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
    const endIndex = Math.min(startIndex + this.pageSize, this.filteredLoans.length);
    this.paginatedLoans = this.filteredLoans.slice(startIndex, endIndex);
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
      'pending': 'Pending',
      'approved': 'Approved',
      'rejected': 'Rejected',
      'completed': 'Completed'
    };
    return statusMap[status] || status;
  }

  getStatusClass(status: string): string {
    const statusClassMap: { [key: string]: string } = {
      'pending': 'status-pending',
      'approved': 'status-approved',
      'rejected': 'status-rejected',
      'completed': 'status-paid'
    };
    return statusClassMap[status] || '';
  }

  /**
   * Ouvre le formulaire d'édition pour un prêt
   * @param loan Le prêt à modifier
   */
  editLoan(loan: Loan): void {
    // Créer une copie du prêt pour éviter de modifier directement l'objet original
    this.selectedLoan = { ...loan };

    // Initialiser le modal si ce n'est pas déjà fait
    if (!this.editModal) {
      const modalElement = document.getElementById('editLoanModal');
      if (modalElement) {
        this.editModal = new bootstrap.Modal(modalElement);
      }
    }

    // Ouvrir le modal
    if (this.editModal) {
      this.editModal.show();
    }
  }

  /**
   * Enregistre les modifications apportées au prêt
   */
  saveLoanChanges(): void {
    if (!this.selectedLoan || !this.selectedLoan.id_loan) {
      console.error('No loan selected or missing ID');
      return;
    }

    // Assurez-vous que le statut est bien une valeur de l'enum backend
    // et non pas le libellé affiché à l'utilisateur
    const loanToUpdate = {...this.selectedLoan};
    const loanId = loanToUpdate.id_loan!;
    const newStatus = loanToUpdate.status || '';

    // Mémoriser la page courante avant la mise à jour
    const currentPageBeforeUpdate = this.currentPage;

    // Afficher les données qui seront envoyées au backend
    console.log('Données du prêt à mettre à jour:', {
      id: loanId,
      amount: loanToUpdate.amount,
      loantype: loanToUpdate.loantype.name,
      interest_rate: loanToUpdate.interest_rate,
      refund_duration: loanToUpdate.refund_duration,
      status: newStatus
    });

    // Mettre à jour d'abord les informations générales du prêt
    this.loanService.updateLoan(loanId, loanToUpdate).subscribe({
      next: (updatedLoan) => {
        console.log('Informations générales du prêt mises à jour avec succès:', updatedLoan);

        // Ensuite, mettre à jour spécifiquement le statut
        this.loanService.updateLoanStatus(loanId, newStatus).subscribe({
          next: (statusUpdatedLoan) => {
            console.log('Statut du prêt mis à jour avec succès:', statusUpdatedLoan);

            // Fermer le modal
            if (this.editModal) {
              this.editModal.hide();
            }

            // Recharger la liste des prêts mais rester sur la même page
            this.loanService.getAllLoans().subscribe({
              next: (data) => {
                console.log('Données reçues du backend après mise à jour:', data);
                this.loans = data;
                this.calculateTotalPages();

                // Vérifier si la page actuelle est toujours valide
                const pageToGo = currentPageBeforeUpdate > this.totalPages
                  ? this.totalPages
                  : currentPageBeforeUpdate;

                // Aller à la même page qu'avant la mise à jour
                this.goToPage(pageToGo);
              },
              error: (err) => {
                console.error('Erreur lors du rechargement des prêts:', err);
                alert('Changes have been saved, but an error occurred while reloading the list.');
              }
            });
          },
          error: (statusErr) => {
            console.error('Erreur lors de la mise à jour du statut du prêt:', statusErr);
            console.error('Détails de l\'erreur:', statusErr.error || statusErr.message || statusErr);
            alert('An error occurred while updating the loan status. Please try again.');
          }
        });
      },
      error: (err) => {
        console.error('Erreur lors de la mise à jour des informations générales du prêt:', err);
        console.error('Détails de l\'erreur:', err.error || err.message || err);
        alert('An error occurred while updating the loan. Please try again.');
      }
    });
  }

  /**
   * Supprime un prêt
   * @param loanId L'ID du prêt à supprimer
   */
  deleteLoan(loanId: number): void {
    if (confirm('Are you sure you want to delete this loan?')) {
      // Mémoriser la page courante avant la suppression
      const currentPageBeforeDelete = this.currentPage;

      this.loanService.deleteLoan(loanId).subscribe({
        next: () => {
          console.log('Prêt supprimé avec succès');

          // Recharger la liste des prêts mais rester sur la même page
          this.loanService.getAllLoans().subscribe({
            next: (data) => {
              console.log('Données reçues du backend après suppression:', data);
              this.loans = data;
              this.calculateTotalPages();

              // Si la page actuelle n'existe plus (par exemple, si on a supprimé le dernier élément de la dernière page)
              const pageToGo = currentPageBeforeDelete > this.totalPages
                ? this.totalPages
                : currentPageBeforeDelete;

              // Aller à la même page qu'avant la suppression (ou à la dernière page si celle-ci n'existe plus)
              this.goToPage(pageToGo);
            },
            error: (err) => {
              console.error('Erreur lors du rechargement des prêts:', err);
              alert('The loan has been deleted, but an error occurred while reloading the list.');
            }
          });
        },
        error: (err) => {
          console.error('Erreur lors de la suppression du prêt:', err);
          alert('An error occurred while deleting the loan. Please try again.');
        }
      });
    }
  }

  /**
   * Télécharge le contrat de prêt
   * @param loanId L'ID du prêt
   */
  downloadContract(loanId: number): void {
    this.loanService.downloadLoanContract(loanId).subscribe({
      next: (blob) => {
        // Créer un objet URL pour le blob
        const url = window.URL.createObjectURL(blob);

        // Créer un élément <a> pour déclencher le téléchargement
        const a = document.createElement('a');
        a.href = url;
        a.download = `loan-contract-${loanId}.pdf`;
        document.body.appendChild(a);
        a.click();

        // Nettoyer
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
      },
      error: (err) => {
        console.error('Erreur lors du téléchargement du contrat:', err);
        alert('Unable to download the contract. Please try again later.');
      }
    });
  }
}
