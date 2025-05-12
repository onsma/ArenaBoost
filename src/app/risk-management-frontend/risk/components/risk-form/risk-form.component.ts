import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RiskService } from '../../services/risk.service';
import { Risk } from '../../models/risk';
import { RiskType } from '../../models/risk-type.enum';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-risk-form',
  templateUrl: './risk-form.component.html',
styleUrls: ['./risk-form.component.scss']})
export class RiskFormComponent implements OnInit {
  riskForm: FormGroup;
  riskTypes = Object.values(RiskType);
  riskId?: number;

  constructor(
/* The line `private fb: FormBuilder` in the constructor of the `RiskFormComponent` class is declaring
a private property `fb` of type `FormBuilder`. */
    private fb: FormBuilder,
    private riskService: RiskService,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.riskForm = this.fb.group({
      score: [0, Validators.required],
      probability: [0, [Validators.required, Validators.min(0)]],
      impact: [0, Validators.required],
      risktype: [RiskType.DEFAULT_OF_THE_CLUB, Validators.required],
      amount: [0, [Validators.required, Validators.min(0)]],
    });
  }

  ngOnInit(): void {
    this.riskId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.riskId) {
      this.riskService.getAllRisks().subscribe((risks) => {
        const risk = risks.find((r) => r.risk_id === this.riskId);
        if (risk) {
          this.riskForm.patchValue(risk);
        }
      });
    }
  }

  onSubmit(): void {
    if (this.riskForm.valid) {
      const riskData: Risk = this.riskForm.value;
      if (this.riskId) {
        this.riskService.updateRisk(this.riskId, riskData).subscribe({
          next: () => {
            this.snackBar.open('Risk updated', 'Close', { duration: 3000 });
            this.router.navigate(['/risk']);
          },
          error: () => this.snackBar.open('Failed to update risk', 'Close', { duration: 3000 }),
        });
      } else {
        this.riskService.addRisk(riskData).subscribe({
          next: () => {
            this.snackBar.open('Risk added', 'Close', { duration: 3000 });
            this.router.navigate(['/risk']);
          },
          error: () => this.snackBar.open('Failed to add risk', 'Close', { duration: 3000 }),
        });
      }
    }
  }
}
