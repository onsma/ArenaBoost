import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import { DashboardService } from '../../services/dashboard.service';
import { LoanStatistics, ActiveUser } from '../../services/dashboard.service';
import { Chart, ChartConfiguration, ChartData, ChartOptions } from 'chart.js';
import 'chart.js/auto';


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  loanStats: LoanStatistics = {
    totalLoans: 0,
    approvedLoans: 0,
    pendingLoans: 0,
    totalAmount: 0
  };
  activeUsers: ActiveUser[] = [];

  @ViewChild('loanStatusChart') private loanStatusChartRef!: ElementRef;
  @ViewChild('loanTypeChartCanvas') private loanTypeChartCanvas!: ElementRef;

  private loanStatusChart!: Chart;

  private loanStatusChartData: ChartData<'bar'> = {
    labels: ['Approuvés', 'En Attente'],
    datasets: [
      {
        label: 'Nombre de Prêts',
        data: [0, 0],
        backgroundColor: ['#2ecc71', '#e74c3c'],
        borderColor: ['#27ae60', '#c0392b'],
        borderWidth: 1
      }
    ]
  };

  private loanStatusChartOptions: ChartOptions<'bar'> = {
    responsive: true,
    scales: {
      y: {
        beginAtZero: true
      }
    }
  };

  constructor(
    private dashboardService: DashboardService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadDashboardData();
  }

  ngAfterViewInit() {
    this.initializeCharts();
    this.loadLoanTypeChart();
  }

  private loadDashboardData() {
    this.dashboardService.getLoanStatistics().subscribe(data => {
      this.loanStats = data;
      this.updateLoanStatusChart();
    });

    this.dashboardService.getActiveUsers().subscribe(data => {
      this.activeUsers = data;
    });
  }

  private initializeCharts() {
    // Loan Status Bar Chart
    this.loanStatusChart = new Chart(this.loanStatusChartRef.nativeElement, {
      type: 'bar',
      data: this.loanStatusChartData,
      options: this.loanStatusChartOptions
    });
  }

  private updateLoanStatusChart() {
    if (this.loanStatusChart && this.loanStats) {
      this.loanStatusChart.data.datasets[0].data = [
        this.loanStats.approvedLoans,
        this.loanStats.pendingLoans
      ];
      this.loanStatusChart.update();
    }
  }


  loanTypeChart: any;

  navigateToLoanList(): void {
    this.router.navigate(['/loan-list']);
  }

  navigateToLoanRequest(): void {
    this.router.navigate(['/loan-request']);
  }

  loadLoanTypeChart(): void {
    this.dashboardService.getLoanTypeStats().subscribe(data => {
      const labels = Object.keys(data).map(key => this.dashboardService.getLoanTypeLabel(key));
      const values = Object.values(data);

      if (this.loanTypeChart) this.loanTypeChart.destroy();

      this.loanTypeChart = new Chart(this.loanTypeChartCanvas.nativeElement, {
        type: 'doughnut',
        data: {
          labels: labels,
          datasets: [{
            data: values,
            backgroundColor: ['#FF6384', '#FFCE56', '#36A2EB', '#4BC0C0', '#9966FF', '#00B894', '#FF9F40', '#C9CBCF', '#7E57C2']
          }]
        },
        options: {
          responsive: true,
          plugins: {
            legend: { position: 'bottom' }
          }
        }
      });
    });
  }
}
