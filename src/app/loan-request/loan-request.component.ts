import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoanService } from '../services/loan.service';
import { LoanTypeService } from '../services/loantype.service';
import { Loan, LoanType } from '../services/models/loan.model';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-loan-request',
  templateUrl: './loan-request.component.html',
  styleUrls: ['./loan-request.component.scss']
})
export class LoanRequestComponent implements OnInit {
  loanForm: FormGroup;
  loading = false;
  error: string = '';
  isConfirmation = false;
  loanId?: number;
  showSuccessMessage = false;
  successMessage = '';

  userId: number = 1; // To be modified to get the connected user's ID

  loanTypes: LoanType[] = [];

  constructor(
    private fb: FormBuilder,
    private loanService: LoanService,
    private loanTypeService: LoanTypeService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.loanForm = this.fb.group({
      userId: ['', [Validators.required]],
      amount: ['', [Validators.required, Validators.min(1000)]],
      loantype: ['', [Validators.required]],
      interest_rate: ['', [Validators.required, Validators.min(0)]],
      refund_duration: ['', [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.loadLoanTypes();
    this.route.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.isConfirmation = true;
        this.loanId = id;
      }
    });
  }

  loadLoanTypes(): void {
    this.loanTypeService.getAllLoanTypes().subscribe(
      (loanTypes: LoanType[]) => {
        this.loanTypes = loanTypes;
      },
      (error: any) => {
        this.error = 'Error loading loan types: ' + error.message;
        console.error('Error loading loan types:', error);
      }
    );
  }



  onSubmit(): void {
    if (this.loanForm.valid) {
      this.loading = true;
      const selectedLoanType = this.loanTypes.find(
        (lt: LoanType) => lt.name === this.loanForm.value.loantype
      );

      const loan: Loan = {
        amount: this.loanForm.value.amount,
        loantype: selectedLoanType || { id_loantype: 0, name: '', description: '' },
        interest_rate: this.loanForm.value.interest_rate,
        refund_duration: this.loanForm.value.refund_duration,
        user: {
          id_user: this.loanForm.value.userId
        }
      };

      // Get the connected user's ID (to be implemented)
      const userId = 1; // To be replaced with real authentication logic

      this.loanService.createLoan(loan).subscribe(
        (response: Loan) => {
          this.loading = false;

          // Display success message
          this.showSuccessMessage = true;
          this.successMessage = 'Your loan request has been submitted successfully!';

          // Redirect after a short delay to allow the user to see the message
          setTimeout(() => {
            this.router.navigate(['/loan-confirmation', response.id_loan]);
          }, 2000);
        },
        (error: any) => {
          this.loading = false;
          this.error = error.message;
        }
      );
    }
  }

  navigateToSimulation(): void {
    this.router.navigate(['/loan-simulation']);
  }
}
