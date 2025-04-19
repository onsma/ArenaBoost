import { Component, OnInit } from '@angular/core';
import Chart from 'chart.js';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  totalLoans: number = 24;

  constructor() { }

  ngOnInit(): void {
    this.initLoanChart();
    this.initActivityChart();
  }

  initLoanChart() {
    if (document.getElementById('loanChart')) {
      const loanChartCanvas = (document.getElementById('loanChart') as HTMLCanvasElement).getContext('2d');
      
      const loanChartData = {
        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
        datasets: [{
          label: 'Prêts approuvés',
          data: [15, 19, 12, 17, 20, 24],
          backgroundColor: '#d4af37', // Gold
          borderColor: '#d4af37',
          borderWidth: 2,
          fill: false
        }, {
          label: 'Prêts en attente',
          data: [8, 12, 5, 9, 12, 15],
          backgroundColor: '#ffffff', // White
          borderColor: '#ffffff',
          borderWidth: 2,
          fill: false
        }]
      };
      
      const loanChartOptions = {
        responsive: true,
        maintainAspectRatio: true,
        scales: {
          yAxes: [{
            ticks: {
              beginAtZero: true,
              fontColor: '#ffffff'
            },
            gridLines: {
              color: 'rgba(255, 255, 255, 0.1)'
            }
          }],
          xAxes: [{
            ticks: {
              fontColor: '#ffffff'
            },
            gridLines: {
              color: 'rgba(255, 255, 255, 0.1)'
            }
          }]
        },
        legend: {
          labels: {
            fontColor: '#ffffff'
          }
        }
      };
      
      new Chart(loanChartCanvas, {
        type: 'line',
        data: loanChartData,
        options: loanChartOptions
      });
    }
  }

  initActivityChart() {
    if (document.getElementById('activityChart')) {
      const activityChartCanvas = (document.getElementById('activityChart') as HTMLCanvasElement).getContext('2d');
      
      const activityChartData = {
        labels: ['Demandes', 'Approuvés', 'En attente', 'Refusés'],
        datasets: [{
          data: [45, 25, 20, 10],
          backgroundColor: [
            '#000000', // Black
            '#d4af37', // Gold
            '#ffffff', // White
            '#555555'  // Gray
          ],
          borderWidth: 0
        }]
      };
      
      const activityChartOptions = {
        responsive: true,
        maintainAspectRatio: true,
        cutoutPercentage: 70,
        legend: {
          display: true,
          position: 'bottom',
          labels: {
            fontColor: '#ffffff',
            boxWidth: 12
          }
        },
        tooltips: {
          enabled: true
        }
      };
      
      new Chart(activityChartCanvas, {
        type: 'doughnut',
        data: activityChartData,
        options: activityChartOptions
      });
    }
  }
}
