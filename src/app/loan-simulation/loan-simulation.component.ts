import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoanSimulationService, LoanSimulationRequest, LoanSimulationResponse } from '../services/loan-simulation.service';
import { LoanTypeService } from '../services/loantype.service';
import { LoanType } from '../services/models/loan.model';

@Component({
  selector: 'app-loan-simulation',
  templateUrl: './loan-simulation.component.html',
  styleUrls: ['./loan-simulation.component.scss']
})
export class LoanSimulationComponent implements OnInit {
  simulationForm: FormGroup;
  loanTypes: LoanType[] = [];
  simulationResult: LoanSimulationResponse | null = null;
  loading = false;
  error = '';
  showResults = false;
  
  // Valeurs par défaut pour les sliders
  minAmount = 1000;
  maxAmount = 100000;
  defaultAmount = 5000;
  
  minDuration = 1;
  maxDuration = 60;
  defaultDuration = 12;
  
  minInterestRate = 0;
  maxInterestRate = 20;
  defaultInterestRate = 5;

  constructor(
    private fb: FormBuilder,
    private loanSimulationService: LoanSimulationService,
    private loanTypeService: LoanTypeService
  ) {
    this.simulationForm = this.fb.group({
      loanType: ['', Validators.required],
      amount: [this.defaultAmount, [Validators.required, Validators.min(this.minAmount), Validators.max(this.maxAmount)]],
      duration: [this.defaultDuration, [Validators.required, Validators.min(this.minDuration), Validators.max(this.maxDuration)]],
      interestRate: [this.defaultInterestRate, [Validators.required, Validators.min(this.minInterestRate), Validators.max(this.maxInterestRate)]]
    });
  }

  ngOnInit(): void {
    this.loadLoanTypes();
    
    // Réagir aux changements de valeurs pour mettre à jour la simulation en temps réel
    this.simulationForm.valueChanges.subscribe(() => {
      if (this.simulationForm.valid) {
        this.simulateLoan();
      }
    });
    
    // Simulation initiale
    this.simulateLoan();
  }

  loadLoanTypes(): void {
    this.loanTypeService.getAllLoanTypes().subscribe(
      (types: LoanType[]) => {
        this.loanTypes = types;
        if (types.length > 0) {
          this.simulationForm.patchValue({ loanType: types[0].name });
        }
      },
      (error) => {
        this.error = 'Erreur lors du chargement des types de prêt: ' + error.message;
        console.error('Error loading loan types:', error);
      }
    );
  }

  simulateLoan(): void {
    if (this.simulationForm.valid) {
      this.loading = true;
      
      const request: LoanSimulationRequest = {
        amount: this.simulationForm.value.amount,
        duration: this.simulationForm.value.duration,
        interestRate: this.simulationForm.value.interestRate
      };
      
      this.loanSimulationService.simulateLoan(request).subscribe(
        (response: LoanSimulationResponse) => {
          this.simulationResult = response;
          this.showResults = true;
          this.loading = false;
        },
        (error) => {
          this.error = 'Erreur lors de la simulation: ' + error.message;
          this.loading = false;
          console.error('Error simulating loan:', error);
          
          // Simulation côté client en cas d'erreur avec le backend
          this.simulateLocally(request);
        }
      );
    }
  }
  
  // Méthode de secours pour simuler le prêt localement si l'API échoue
  simulateLocally(request: LoanSimulationRequest): void {
    const amount = request.amount;
    const duration = request.duration;
    const interestRate = request.interestRate;
    
    // Conversion du taux d'intérêt annuel en taux mensuel
    const monthlyRate = (interestRate / 100) / 12;
    
    // Calcul de la mensualité avec la formule de l'annuité
    const monthlyPayment = (amount * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -duration));
    
    // Calcul du coût total et des intérêts
    const totalAmount = monthlyPayment * duration;
    const totalInterest = totalAmount - amount;
    
    this.simulationResult = {
      monthlyPayment,
      totalInterest,
      totalAmount
    };
    
    this.showResults = true;
  }
  
  // Formatter les valeurs pour l'affichage
  formatAmount(value: number): string {
    return value.toLocaleString('fr-FR') + ' DT';
  }
  
  formatDuration(value: number): string {
    return value + (value > 1 ? ' mois' : ' mois');
  }
  
  formatInterestRate(value: number): string {
    return value + ' %';
  }
  
  // Réinitialiser le formulaire
  resetForm(): void {
    this.simulationForm.reset({
      loanType: this.loanTypes.length > 0 ? this.loanTypes[0].name : '',
      amount: this.defaultAmount,
      duration: this.defaultDuration,
      interestRate: this.defaultInterestRate
    });
    this.simulateLoan();
  }
}
