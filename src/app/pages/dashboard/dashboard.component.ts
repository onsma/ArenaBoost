import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { DashboardService } from '../../services/dashboard.service';
import { LoanStatistics, ActiveUser, RecentActivity } from '../../services/dashboard.service';
import { Chart, ChartConfiguration, ChartData, ChartOptions } from 'chart.js';
import 'chart.js/auto';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  loanStats: LoanStatistics | null = null;
  activeUsers: ActiveUser[] = [];

  @ViewChild('loanStatusChart') private loanStatusChartRef!: ElementRef;
  @ViewChild('activityChart') private activityChartRef!: ElementRef;

  private loanStatusChart!: Chart;
  private activityChart!: Chart;

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

  private activityChartData: ChartData<'doughnut'> = {
    labels: [],
    datasets: [
      {
        data: [],
        backgroundColor: [
          '#2ecc71',
          '#3498db',
          '#9b59b6',
          '#e74c3c',
          '#f1c40f'
        ]
      }
    ]
  };

  private recentActivity: RecentActivity[] = [];

  private activityChartOptions: ChartOptions<'doughnut'> = {
    responsive: true,
    plugins: {
      legend: {
        position: 'bottom'
      }
    }
  };

  constructor(private dashboardService: DashboardService) {}

  ngOnInit() {
    this.loadDashboardData();
  }

  ngAfterViewInit() {
    this.initializeCharts();
  }

  private loadDashboardData() {
    this.dashboardService.getLoanStatistics().subscribe(data => {
      this.loanStats = data;
      this.updateLoanStatusChart();
    });

    this.dashboardService.getActiveUsers().subscribe(data => {
      this.activeUsers = data;
    });

    this.dashboardService.getRecentActivity().subscribe(data => {
      this.recentActivity = data;
      this.updateActivityChart();
    });
  }

  private initializeCharts() {
    // Loan Status Bar Chart
    this.loanStatusChart = new Chart(this.loanStatusChartRef.nativeElement, {
      type: 'bar',
      data: this.loanStatusChartData,
      options: this.loanStatusChartOptions
    });

    // Activity Donut Chart
    this.activityChart = new Chart(this.activityChartRef.nativeElement, {
      type: 'doughnut',
      data: this.activityChartData,
      options: this.activityChartOptions
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

  private updateActivityChart() {
    if (this.activityChart && this.recentActivity.length > 0) {
      const labels = this.recentActivity.map(activity => activity.activityType);
      const data = this.recentActivity.map(activity => activity.count);
      
      this.activityChart.data.labels = labels;
      this.activityChart.data.datasets[0].data = data;
      this.activityChart.update();
    }
  }
}
