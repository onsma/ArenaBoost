import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { RiskService } from '../../services/risk.service';
import { Risk } from '../../models/risk';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';


@Component({
  selector: 'app-risk-list',
  templateUrl: './risk-list.component.html',
  styleUrls: ['./risk-list.component.scss'],
})
export class RiskListComponent implements OnInit {
  dataSource = new MatTableDataSource<Risk>([]); // Initialize with empty array
  displayedColumns: string[] = [
    'risk_id',
    'score',
    'probability',
    'impact',
    'riskType',
    'amount',
    'actions',
  ];
  isLoading = true; // Add loading state

  constructor(
    private riskService: RiskService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.fetchRisks();
  }

 fetchRisks(): void {
  this.isLoading = true;
  this.riskService.getAllRisks().subscribe({
    next: (risks) => {
      this.dataSource.data = risks;
      this.isLoading = false;
    },
    error: () => {
      this.snackBar.open('Failed to fetch risks', 'Close', { duration: 3000 });
      this.isLoading = false;
    }
  });
}

  deleteRisk(id: number): void {
    this.riskService.deleteRisk(id).subscribe({
      next: () => {
        this.dataSource.data = this.dataSource.data.filter((risk) => risk.risk_id !== id);
        this.snackBar.open('Risk deleted', 'Close', { duration: 3000 });
      },
      error: () => this.snackBar.open('Failed to delete risk', 'Close', { duration: 3000 }),
    });
  }

  calculateScore(id: number): void {
    this.riskService.calculateRiskScore(id).subscribe({
      next: (score) => this.snackBar.open(`Risk Score: ${score}`, 'Close', { duration: 3000 }),
      error: () => this.snackBar.open('Failed to calculate score', 'Close', { duration: 3000 }),
    });
  }

  checkRisk(id: number): void {
    this.riskService.checkRiskAndNotify(id).subscribe({
      next: (message) => this.snackBar.open(message, 'Close', { duration: 3000 }),
      error: () => this.snackBar.open('Failed to check risk', 'Close', { duration: 3000 }),
    });
  }

  downloadReport(): void {
    this.riskService.generateReport().subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'risk_report.pdf';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => this.snackBar.open('Failed to download report', 'Close', { duration: 3000 }),
    });
  }

  editRisk(risk: Risk): void {
    this.router.navigate(['/risk/edit', risk.risk_id]);
  }

  navigateToAddRisk(): void {
    this.router.navigate(['/risk/add']);
  }
}
