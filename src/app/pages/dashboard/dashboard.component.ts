import { Component, OnInit, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { catchError, of } from 'rxjs';
import { Chart, registerables } from 'chart.js';

import { ProjectService } from '../../services/project.service';
import { DashboardService } from '../../services/dashboard.service';
import {
  DashboardData,
  DashboardMetrics,
  FinancialSnapshot,
  ProjectProgress,
  ProjectRecommendation,
  TopProject,
  UrgentProject,
  UserContribution
} from '../../models/dashboard.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, AfterViewInit {
  // Register Chart.js components
  constructor(
    private projectService: ProjectService,
    private dashboardService: DashboardService,
    private router: Router,
    private http: HttpClient
  ) {
    // Register all Chart.js components
    Chart.register(...registerables);
  }

  // Flag to track if view is initialized
  private viewInitialized = false;
  // Dashboard data
  dashboardData: DashboardData | null = null;
  loading = true;
  error = false;
  errorMessage = '';
  usingMockData = false;
  backendConnected = false;

  // Chart instances
  private financialChart: Chart | null = null;
  private lineChart: Chart | null = null;
  private radarChart: Chart | null = null;
  private polarChart: Chart | null = null;

  // Chart data and options
  public barChartOptions: any = {
    responsive: true,
    maintainAspectRatio: false,
    indexAxis: 'y', // Horizontal bar chart for better readability
    scales: {
      x: {
        min: 0,
        max: 100,
        grid: {
          display: false
        },
        ticks: {
          callback: (value: any) => `${value}%`
        }
      },
      y: {
        grid: {
          display: false
        }
      }
    },
    plugins: {
      legend: {
        display: false, // Hide legend as it's obvious what the bars represent
      },
      tooltip: {
        callbacks: {
          label: (context: any) => `Progress: ${context.raw}%`
        }
      }
    }
  };
  public barChartType: string = 'bar';
  public barChartData: any = {
    labels: [],
    datasets: [
      {
        data: [],
        label: 'Progress (%)',
        backgroundColor: [
          'rgba(201, 176, 55, 0.8)', // Gold
          'rgba(39, 61, 78, 0.8)',   // Dark blue
          'rgba(108, 117, 125, 0.8)', // Gray
          'rgba(40, 167, 69, 0.8)',  // Green
          'rgba(220, 53, 69, 0.8)'   // Red
        ],
        borderColor: [
          'rgb(201, 176, 55)',
          'rgb(39, 61, 78)',
          'rgb(108, 117, 125)',
          'rgb(40, 167, 69)',
          'rgb(220, 53, 69)'
        ],
        borderWidth: 1,
        borderRadius: 4
      }
    ]
  };

  // Financial pie chart
  public pieChartOptions: any = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'right',
        labels: {
          boxWidth: 15,
          padding: 15,
          font: {
            size: 12
          }
        }
      },
      tooltip: {
        callbacks: {
          label: (context: any) => {
            const value = context.raw as number;
            let total = 0;
            if (context.dataset.data && Array.isArray(context.dataset.data)) {
              total = context.dataset.data.reduce((sum: number, val: any) => {
                return sum + (Number(val) || 0);
              }, 0);
            }
            const percentage = total > 0 ? Math.round((value / total) * 100) : 0;
            return `${context.label}: ${this.formatCurrency(value)} (${percentage}%)`;
          }
        }
      },
      datalabels: {
        formatter: (value: any, ctx: any) => {
          let total = 0;
          if (ctx.dataset.data && Array.isArray(ctx.dataset.data)) {
            total = ctx.dataset.data.reduce((sum: number, val: any) => {
              return sum + (Number(val) || 0);
            }, 0);
          }
          const percentage = total > 0 ? Math.round((value / total) * 100) : 0;
          return percentage > 5 ? `${percentage}%` : '';
        },
        color: '#fff',
        font: {
          weight: 'bold'
        }
      }
    }
  };
  public pieChartType: string = 'doughnut'; // Doughnut looks more modern than pie
  public pieChartData: any = {
    labels: ['Available Funds', 'Expenses'],
    datasets: [
      {
        data: [0, 0],
        backgroundColor: [
          'rgba(40, 167, 69, 0.8)',  // Green for funds
          'rgba(220, 53, 69, 0.8)'   // Red for expenses
        ],
        borderColor: [
          'rgb(40, 167, 69)',
          'rgb(220, 53, 69)'
        ],
        borderWidth: 1
      }
    ]
  };

  // Category pie chart
  public categoryChartOptions: any = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'right',
        labels: {
          boxWidth: 15,
          padding: 15,
          font: {
            size: 12
          }
        }
      },
      tooltip: {
        callbacks: {
          label: (context: any) => {
            const value = context.raw as number;
            let total = 0;
            if (context.dataset.data && Array.isArray(context.dataset.data)) {
              total = context.dataset.data.reduce((sum: number, val: any) => {
                return sum + (Number(val) || 0);
              }, 0);
            }
            const percentage = total > 0 ? Math.round((value / total) * 100) : 0;
            return `${context.label}: ${this.formatCurrency(value)} (${percentage}%)`;
          }
        }
      },
      datalabels: {
        formatter: (value: any, ctx: any) => {
          let total = 0;
          if (ctx.dataset.data && Array.isArray(ctx.dataset.data)) {
            total = ctx.dataset.data.reduce((sum: number, val: any) => {
              return sum + (Number(val) || 0);
            }, 0);
          }
          const percentage = total > 0 ? Math.round((value / total) * 100) : 0;
          return percentage > 5 ? `${percentage}%` : '';
        },
        color: '#fff',
        font: {
          weight: 'bold'
        }
      }
    }
  };
  public categoryChartType: string = 'pie';
  public categoryChartData: any = {
    labels: [],
    datasets: [
      {
        data: [],
        backgroundColor: [
          'rgba(201, 176, 55, 0.8)',  // Gold
          'rgba(39, 61, 78, 0.8)',    // Dark blue
          'rgba(108, 117, 125, 0.8)', // Gray
          'rgba(40, 167, 69, 0.8)',   // Green
          'rgba(220, 53, 69, 0.8)',   // Red
          'rgba(23, 162, 184, 0.8)',  // Cyan
          'rgba(253, 126, 20, 0.8)'   // Orange
        ],
        borderColor: [
          'rgb(201, 176, 55)',
          'rgb(39, 61, 78)',
          'rgb(108, 117, 125)',
          'rgb(40, 167, 69)',
          'rgb(220, 53, 69)',
          'rgb(23, 162, 184)',
          'rgb(253, 126, 20)'
        ],
        borderWidth: 1
      }
    ]
  };

  // Line chart for funding trends
  public lineChartOptions: any = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      x: {
        grid: {
          display: false
        }
      },
      y: {
        beginAtZero: true,
        ticks: {
          callback: (value: any) => this.formatCurrency(Number(value))
        }
      }
    },
    plugins: {
      legend: {
        display: true,
        position: 'top'
      },
      tooltip: {
        callbacks: {
          label: (context: any) => {
            const value = context.raw as number;
            return `${context.dataset.label}: ${this.formatCurrency(value)}`;
          }
        }
      }
    }
  };
  public lineChartType: string = 'line';
  public lineChartData: any = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
    datasets: [
      {
        data: [0, 0, 0, 0, 0, 0],
        label: 'Funds Raised',
        borderColor: 'rgba(40, 167, 69, 1)',
        backgroundColor: 'rgba(40, 167, 69, 0.2)',
        pointBackgroundColor: 'rgba(40, 167, 69, 1)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(40, 167, 69, 1)',
        tension: 0.4,
        fill: true
      },
      {
        data: [0, 0, 0, 0, 0, 0],
        label: 'Expenses',
        borderColor: 'rgba(220, 53, 69, 1)',
        backgroundColor: 'rgba(220, 53, 69, 0.2)',
        pointBackgroundColor: 'rgba(220, 53, 69, 1)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(220, 53, 69, 1)',
        tension: 0.4,
        fill: true
      }
    ]
  };

  // Radar chart for project performance metrics
  public radarChartOptions: any = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      r: {
        beginAtZero: true,
        max: 100,
        ticks: {
          display: false
        }
      }
    },
    plugins: {
      legend: {
        display: true,
        position: 'top'
      },
      tooltip: {
        callbacks: {
          label: (context: any) => {
            const value = context.raw as number;
            return `${context.dataset.label}: ${value}%`;
          }
        }
      }
    }
  };
  public radarChartType: string = 'radar';
  public radarChartData: any = {
    labels: ['Funding', 'Timeline', 'Engagement', 'Risk', 'Innovation', 'Impact'],
    datasets: [
      {
        data: [0, 0, 0, 0, 0, 0],
        label: 'Project Performance',
        backgroundColor: 'rgba(201, 176, 55, 0.2)',
        borderColor: 'rgba(201, 176, 55, 1)',
        pointBackgroundColor: 'rgba(201, 176, 55, 1)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(201, 176, 55, 1)'
      }
    ]
  };

  // Polar area chart for project categories distribution
  public polarChartOptions: any = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      r: {
        ticks: {
          display: false
        }
      }
    },
    plugins: {
      legend: {
        display: true,
        position: 'right',
        labels: {
          boxWidth: 15,
          padding: 15,
          font: {
            size: 12
          }
        }
      },
      tooltip: {
        callbacks: {
          label: (context: any) => {
            const value = context.raw as number;
            return `${context.label}: ${value} projects`;
          }
        }
      }
    }
  };
  public polarChartType: string = 'polarArea';
  public polarChartData: any = {
    labels: [],
    datasets: [
      {
        data: [],
        backgroundColor: [
          'rgba(201, 176, 55, 0.7)',  // Gold
          'rgba(39, 61, 78, 0.7)',    // Dark blue
          'rgba(108, 117, 125, 0.7)', // Gray
          'rgba(40, 167, 69, 0.7)',   // Green
          'rgba(220, 53, 69, 0.7)',   // Red
          'rgba(23, 162, 184, 0.7)',  // Cyan
          'rgba(253, 126, 20, 0.7)'   // Orange
        ],
        borderWidth: 1
      }
    ]
  };



  ngOnInit(): void {
    this.loadDashboardData();
  }

  ngAfterViewInit(): void {
    this.viewInitialized = true;

    // If we already have data, update the charts
    if (this.dashboardData) {
      this.updateCharts();
    }
  }

  loadDashboardData(): void {
    this.loading = true;
    this.error = false;
    this.usingMockData = false;
    this.backendConnected = false;
    console.log('Loading dashboard data from real database...');

    // Load dashboard data directly from our service that connects to the real database
    this.dashboardService.getDashboardData().subscribe({
      next: (data) => {
        console.log('Dashboard data loaded successfully from backend:', data);
        this.dashboardData = data;
        this.backendConnected = true;
        this.usingMockData = false;

        // Update charts if view is initialized
        if (this.dashboardData) {
          console.log('Dashboard data loaded, updating charts');
          this.updateCharts();
        }

        this.loading = false;
        this.error = false;
      },
      error: (error) => {
        console.error('Error loading dashboard data from backend:', error);
        this.fallbackToMockData();
      }
    });
  }

  /**
   * Fallback to mock data when backend is not available
   */
  private fallbackToMockData(): void {
    console.log('Falling back to mock data');
    this.error = false;
    this.loading = false;
    this.usingMockData = true;
    this.backendConnected = false;

    // Use the project service's mock data as fallback
    this.projectService.getDashboardData().subscribe({
      next: (data) => {
        console.log('Mock dashboard data loaded successfully:', data);
        this.dashboardData = data;

        // Update charts if view is initialized
        if (this.dashboardData) {
          console.log('Mock dashboard data loaded, updating charts');
          this.updateCharts();
        }
      },
      error: (error) => {
        console.error('Error loading mock dashboard data:', error);
        this.error = true;
        this.errorMessage = 'Failed to load dashboard data. Please try again later.';
      }
    });
  }

  updateCharts(): void {
    if (!this.dashboardData) return;

    // Only create charts if the view is initialized
    if (!this.viewInitialized) {
      console.log('View not initialized yet, charts will be created after view init');
      return;
    }

    console.log('Creating charts with data:', this.dashboardData);

    // Use setTimeout to ensure DOM is fully rendered
    setTimeout(() => {
      try {
        // Create or update financial chart (hidden but still initialized for other charts to work)
        this.createFinancialChart();

        // Create or update line chart for funding trends
        this.createLineChart();

        // Create or update radar chart for project performance
        this.createRadarChart();

        // Create or update polar chart for project categories
        this.createPolarChart();

        console.log('All charts created successfully');
      } catch (error) {
        console.error('Error creating charts:', error);
      }
    }, 100);
  }

  updateProgressChart(): void {
    if (!this.dashboardData || !this.dashboardData.projectProgress) {
      console.warn('No project progress data available for chart');
      return;
    }

    try {
      const projects = this.dashboardData.projectProgress;
      console.log('Updating progress chart with data:', projects);

      // Sort by progress percentage (descending)
      const sortedProjects = [...projects].sort((a, b) => b.progressPercentage - a.progressPercentage);

      // Take top 5 projects for better visualization
      const topProjects = sortedProjects.slice(0, 5);

      // Truncate long project names for better display
      const truncateProjectName = (name: string) => {
        return name.length > 20 ? name.substring(0, 17) + '...' : name;
      };

      this.barChartData.labels = topProjects.map(p => truncateProjectName(p.projectName));
      this.barChartData.datasets[0].data = topProjects.map(p => p.progressPercentage);

      // Dynamically set colors based on progress percentage
      const backgroundColors = topProjects.map(p => {
        if (p.progressPercentage >= 70) return 'rgba(40, 167, 69, 0.8)';  // Green for high progress
        if (p.progressPercentage >= 40) return 'rgba(201, 176, 55, 0.8)'; // Gold for medium progress
        return 'rgba(220, 53, 69, 0.8)';  // Red for low progress
      });

      const borderColors = topProjects.map(p => {
        if (p.progressPercentage >= 70) return 'rgb(40, 167, 69)';  // Green
        if (p.progressPercentage >= 40) return 'rgb(201, 176, 55)'; // Gold
        return 'rgb(220, 53, 69)';  // Red
      });

      // Update colors
      this.barChartData.datasets[0].backgroundColor = backgroundColors;
      this.barChartData.datasets[0].borderColor = borderColors;

      console.log('Bar chart data updated:', this.barChartData);
    } catch (error) {
      console.error('Error updating progress chart:', error);
    }
  }

  createFinancialChart(): void {
    if (!this.dashboardData || !this.dashboardData.financialSnapshot) {
      console.warn('No financial snapshot data available for chart');
      return;
    }

    try {
      // Get top projects for the bar chart
      const projects = this.dashboardData.topProjects.slice(0, 5);
      console.log('Initializing hidden financial chart');

      // Destroy existing chart if it exists
      if (this.financialChart) {
        this.financialChart.destroy();
      }

      // Get the canvas element
      const canvas = document.getElementById('financialChart') as HTMLCanvasElement;
      if (!canvas) {
        console.error('Financial chart canvas not found. DOM element with id "financialChart" does not exist.');
        return;
      }

      // Create the chart - a bar chart showing funds raised by each project
      this.financialChart = new Chart(canvas, {
        type: 'bar',
        data: {
          labels: projects.map(p => p.projectName),
          datasets: [{
            label: 'Funds Raised',
            data: projects.map(p => p.fundsRaised),
            backgroundColor: [
              'rgba(40, 167, 69, 0.8)',   // Green
              'rgba(23, 162, 184, 0.8)',  // Cyan
              'rgba(201, 176, 55, 0.8)',  // Gold
              'rgba(253, 126, 20, 0.8)',  // Orange
              'rgba(108, 117, 125, 0.8)'  // Gray
            ],
            borderColor: [
              'rgb(40, 167, 69)',
              'rgb(23, 162, 184)',
              'rgb(201, 176, 55)',
              'rgb(253, 126, 20)',
              'rgb(108, 117, 125)'
            ],
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                callback: (value) => this.formatCurrency(Number(value))
              }
            }
          },
          plugins: {
            legend: {
              display: false
            },
            tooltip: {
              callbacks: {
                label: (context) => {
                  const value = context.raw as number;
                  return `Funds Raised: ${this.formatCurrency(value)}`;
                }
              }
            }
          }
        }
      });

      console.log('Financial chart created');
    } catch (error) {
      console.error('Error creating financial chart:', error);
    }
  }

  updateCategoryChart(): void {
    if (!this.dashboardData || !this.dashboardData.financialSnapshot || !this.dashboardData.financialSnapshot.fundsByCategory) {
      console.warn('No category data available for chart');
      return;
    }

    try {
      const { fundsByCategory } = this.dashboardData.financialSnapshot;
      console.log('Updating category chart with data:', fundsByCategory);

      // Filter out categories with zero amount
      const nonZeroCategories = fundsByCategory.filter(c => c.amount > 0);

      // Sort categories by amount (descending) for better visualization
      const sortedCategories = [...nonZeroCategories].sort((a, b) => b.amount - a.amount);

      // Format category names to be more readable
      const formatCategoryName = (category: string) => {
        // Convert enum-style names to readable format
        return category
          .replace(/([A-Z])/g, ' $1') // Add space before capital letters
          .replace(/^./, str => str.toUpperCase()); // Capitalize first letter
      };

      this.categoryChartData.labels = sortedCategories.map(c => formatCategoryName(c.category));
      this.categoryChartData.datasets[0].data = sortedCategories.map(c => c.amount);

      // Use a fixed set of colors based on the number of categories
      const colors = [
        'rgba(201, 176, 55, 0.8)',  // Gold
        'rgba(39, 61, 78, 0.8)',    // Dark blue
        'rgba(108, 117, 125, 0.8)', // Gray
        'rgba(40, 167, 69, 0.8)',   // Green
        'rgba(220, 53, 69, 0.8)',   // Red
        'rgba(23, 162, 184, 0.8)',  // Cyan
        'rgba(253, 126, 20, 0.8)'   // Orange
      ];

      const borderColors = [
        'rgb(201, 176, 55)',
        'rgb(39, 61, 78)',
        'rgb(108, 117, 125)',
        'rgb(40, 167, 69)',
        'rgb(220, 53, 69)',
        'rgb(23, 162, 184)',
        'rgb(253, 126, 20)'
      ];

      // If we need more colors, generate them
      if (sortedCategories.length > colors.length) {
        const additionalColors = this.generateColors(sortedCategories.length - colors.length);
        const additionalBorders = additionalColors.map(color =>
          color.replace('rgba', 'rgb').replace(/,[^,]*\)$/, ')')
        );

        this.categoryChartData.datasets[0].backgroundColor = [...colors, ...additionalColors];
        this.categoryChartData.datasets[0].borderColor = [...borderColors, ...additionalBorders];
      } else {
        // Use just the colors we need
        this.categoryChartData.datasets[0].backgroundColor = colors.slice(0, sortedCategories.length);
        this.categoryChartData.datasets[0].borderColor = borderColors.slice(0, sortedCategories.length);
      }

      console.log('Category chart data updated:', this.categoryChartData);
    } catch (error) {
      console.error('Error updating category chart:', error);
    }
  }

  /**
   * Create line chart for funding trends
   */
  createLineChart(): void {
    if (!this.dashboardData || !this.dashboardData.financialSnapshot) {
      console.warn('No financial data available for line chart');
      return;
    }

    try {
      // Generate monthly data for the last 6 months
      const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'];
      const currentMonth = new Date().getMonth();

      // Create labels for the last 6 months
      const labels = [];
      for (let i = 5; i >= 0; i--) {
        const monthIndex = (currentMonth - i + 12) % 12; // Handle wrapping around to previous year
        labels.push(months[monthIndex]);
      }

      // Get top projects for the line chart
      const projects = this.dashboardData.topProjects.slice(0, 3);

      // Generate datasets for each project
      const datasets = projects.map((project, index) => {
        // Generate some realistic funding data based on total funds
        const totalFunds = project.fundsRaised;
        const fundsData = [];

        // Start with some initial value
        let currentFunds = totalFunds * 0.4; // Start at 40% of total

        for (let i = 0; i < 6; i++) {
          // Add some randomness to the growth
          const fundGrowth = 1 + (Math.random() * 0.2); // 0-20% growth
          currentFunds = currentFunds * fundGrowth;

          // Ensure we don't exceed the total
          currentFunds = Math.min(currentFunds, totalFunds);
          fundsData.push(Math.round(currentFunds));
        }

        // Get color for this project
        const color = this.getProjectColor(index);

        return {
          label: project.projectName,
          data: fundsData,
          borderColor: color,
          backgroundColor: color.replace('rgb', 'rgba').replace(')', ', 0.2)'),
          pointBackgroundColor: color,
          pointBorderColor: '#fff',
          pointHoverBackgroundColor: '#fff',
          pointHoverBorderColor: color,
          tension: 0.4,
          fill: true
        };
      });

      // Destroy existing chart if it exists
      if (this.lineChart) {
        this.lineChart.destroy();
      }

      // Get the canvas element
      const canvas = document.getElementById('lineChart') as HTMLCanvasElement;
      if (!canvas) {
        console.error('Line chart canvas not found. DOM element with id "lineChart" does not exist.');
        console.log('Available canvas elements:', document.querySelectorAll('canvas'));
        return;
      }

      console.log('Found line chart canvas:', canvas);

      // Create the chart
      this.lineChart = new Chart(canvas, {
        type: 'line',
        data: {
          labels: labels,
          datasets: datasets
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            x: {
              grid: {
                display: false
              }
            },
            y: {
              beginAtZero: true,
              ticks: {
                callback: (value) => this.formatCurrency(Number(value))
              }
            }
          },
          plugins: {
            legend: {
              display: true,
              position: 'top'
            },
            tooltip: {
              callbacks: {
                label: (context) => {
                  const value = context.raw as number;
                  return `${context.dataset.label}: ${this.formatCurrency(value)}`;
                }
              }
            }
          }
        }
      });

      console.log('Line chart created');
    } catch (error) {
      console.error('Error creating line chart:', error);
    }
  }

  /**
   * Create radar chart for project performance metrics
   */
  createRadarChart(): void {
    if (!this.dashboardData || this.dashboardData.topProjects.length === 0) {
      console.warn('No project data available for radar chart');
      return;
    }

    try {
      // Get the top project for the radar chart
      const topProject = this.dashboardData.topProjects[0];

      // Calculate metrics for the radar chart
      // These would normally come from the backend, but we'll generate them here
      const fundingScore = Math.min(100, (topProject.fundsRaised / (this.dashboardData.financialSnapshot.totalFunds * 0.5)) * 100);
      const timelineScore = topProject.daysRemaining > 30 ? 90 : (topProject.daysRemaining / 30) * 100;
      const engagementScore = Math.random() * 40 + 60; // Random score between 60-100
      const riskScore = 100 - (Math.random() * 30 + 20); // Random score between 50-80
      const innovationScore = Math.random() * 50 + 50; // Random score between 50-100
      const impactScore = Math.random() * 40 + 60; // Random score between 60-100

      // Prepare the data
      const data = [
        Math.round(fundingScore),
        Math.round(timelineScore),
        Math.round(engagementScore),
        Math.round(riskScore),
        Math.round(innovationScore),
        Math.round(impactScore)
      ];

      // Destroy existing chart if it exists
      if (this.radarChart) {
        this.radarChart.destroy();
      }

      // Get the canvas element
      const canvas = document.getElementById('radarChart') as HTMLCanvasElement;
      if (!canvas) {
        console.error('Radar chart canvas not found. DOM element with id "radarChart" does not exist.');
        console.log('Available canvas elements:', document.querySelectorAll('canvas'));
        return;
      }

      console.log('Found radar chart canvas:', canvas);

      // Create the chart
      this.radarChart = new Chart(canvas, {
        type: 'radar',
        data: {
          labels: ['Funding', 'Timeline', 'Engagement', 'Risk', 'Innovation', 'Impact'],
          datasets: [{
            label: `${topProject.projectName} Performance`,
            data: data,
            backgroundColor: 'rgba(201, 176, 55, 0.2)',
            borderColor: 'rgba(201, 176, 55, 1)',
            pointBackgroundColor: 'rgba(201, 176, 55, 1)',
            pointBorderColor: '#fff',
            pointHoverBackgroundColor: '#fff',
            pointHoverBorderColor: 'rgba(201, 176, 55, 1)'
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            r: {
              beginAtZero: true,
              max: 100,
              ticks: {
                display: false
              }
            }
          },
          plugins: {
            legend: {
              display: true,
              position: 'top'
            },
            tooltip: {
              callbacks: {
                label: (context) => {
                  const value = context.raw as number;
                  return `${context.dataset.label}: ${value}%`;
                }
              }
            }
          }
        }
      });

      console.log('Radar chart created');
    } catch (error) {
      console.error('Error creating radar chart:', error);
    }
  }

  /**
   * Create polar chart for project categories
   */
  createPolarChart(): void {
    if (!this.dashboardData) {
      console.warn('No project data available for polar chart');
      return;
    }

    try {
      // Count projects by category
      const categoryMap = new Map<string, number>();

      // Get all projects from the dashboard data
      const projects = [
        ...this.dashboardData.topProjects,
        ...this.dashboardData.urgentProjects,
        ...this.dashboardData.recommendations
      ];

      // Count unique projects by ID
      const uniqueProjects = new Map<number, any>();
      projects.forEach(project => {
        if (!uniqueProjects.has(project.projectId)) {
          uniqueProjects.set(project.projectId, project);

          // Count by category - need to handle different project types
          let category = 'Other';

          // Check if it's a recommendation which has category property
          if ('category' in project) {
            category = project.category || 'Other';
          }

          const count = categoryMap.get(category) || 0;
          categoryMap.set(category, count + 1);
        }
      });

      // Convert to arrays for the chart
      const categories = Array.from(categoryMap.keys());
      const counts = Array.from(categoryMap.values());

      // Format category names
      const formattedCategories = categories.map(category => this.formatCategoryName(category));

      // Destroy existing chart if it exists
      if (this.polarChart) {
        this.polarChart.destroy();
      }

      // Get the canvas element
      const canvas = document.getElementById('polarChart') as HTMLCanvasElement;
      if (!canvas) {
        console.error('Polar chart canvas not found. DOM element with id "polarChart" does not exist.');
        console.log('Available canvas elements:', document.querySelectorAll('canvas'));
        return;
      }

      console.log('Found polar chart canvas:', canvas);

      // Create the chart
      this.polarChart = new Chart(canvas, {
        type: 'polarArea',
        data: {
          labels: formattedCategories,
          datasets: [{
            data: counts,
            backgroundColor: [
              'rgba(201, 176, 55, 0.7)',  // Gold
              'rgba(39, 61, 78, 0.7)',    // Dark blue
              'rgba(108, 117, 125, 0.7)', // Gray
              'rgba(40, 167, 69, 0.7)',   // Green
              'rgba(220, 53, 69, 0.7)',   // Red
              'rgba(23, 162, 184, 0.7)',  // Cyan
              'rgba(253, 126, 20, 0.7)'   // Orange
            ],
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            r: {
              ticks: {
                display: false
              }
            }
          },
          plugins: {
            legend: {
              display: true,
              position: 'right',
              labels: {
                boxWidth: 15,
                padding: 15,
                font: {
                  size: 12
                }
              }
            },
            tooltip: {
              callbacks: {
                label: (context) => {
                  const value = context.raw as number;
                  return `${context.label}: ${value} projects`;
                }
              }
            }
          }
        }
      });

      console.log('Polar chart created');
    } catch (error) {
      console.error('Error creating polar chart:', error);
    }
  }

  // Helper method to generate colors for charts
  private generateColors(count: number): string[] {
    const baseColors = ['#c9b037', '#273d4e', '#6c757d', '#28a745', '#dc3545', '#17a2b8', '#fd7e14'];
    const colors: string[] = [];

    for (let i = 0; i < count; i++) {
      if (i < baseColors.length) {
        colors.push(baseColors[i]);
      } else {
        // Generate a random color if we run out of base colors
        const r = Math.floor(Math.random() * 200);
        const g = Math.floor(Math.random() * 200);
        const b = Math.floor(Math.random() * 200);
        colors.push(`rgb(${r}, ${g}, ${b})`);
      }
    }

    return colors;
  }

  // Helper method to format currency values
  formatCurrency(value: number): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(value);
  }

  navigateToProject(projectId: number): void {
    this.router.navigate(['/projects', projectId]);
  }

  navigateToCreateProject(): void {
    this.router.navigate(['/projects/create']);
  }

  // Helper method to determine progress bar class based on percentage
  getProgressBarClass(percentage: number): string {
    if (percentage >= 75) return 'bg-success';
    if (percentage >= 50) return 'bg-info';
    if (percentage >= 25) return 'bg-warning';
    return 'bg-danger';
  }

  // Helper method to determine days remaining class
  getDaysRemainingClass(days: number): string {
    if (days <= 0) return 'text-danger';
    if (days <= 7) return 'text-warning';
    if (days <= 14) return 'text-info';
    return 'text-success';
  }

  getSuccessRateClass(rate: number): string {
    if (rate >= 70) return 'bg-success';
    if (rate >= 40) return 'bg-warning';
    return 'bg-danger';
  }

  // Helper method to format category names
  formatCategoryName(category: string): string {
    // Convert enum-style names to readable format
    return category
      .replace(/([A-Z])/g, ' $1') // Add space before capital letters
      .replace(/^./, str => str.toUpperCase()); // Capitalize first letter
  }

  // Helper method to calculate category percentage
  getCategoryPercentage(amount: number, total: number): number {
    if (total <= 0) return 0;
    return Math.round((amount / total) * 100);
  }

  // Helper method to get financial percentage for donut chart
  getFinancialPercentage(): number {
    if (!this.dashboardData || !this.dashboardData.financialSnapshot) {
      return 75; // Default value
    }

    const { totalFunds, totalExpenses } = this.dashboardData.financialSnapshot;
    if (totalFunds <= 0) return 0;

    const netFunds = totalFunds - totalExpenses;
    return Math.round((netFunds / totalFunds) * 100);
  }

  // Helper method to get months for line chart
  getMonths(): string[] {
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'];
    return months;
  }

  // Helper method to get funds bar height for line chart
  getFundsBarHeight(index: number): number {
    // Sample data - in a real app, this would come from the backend
    const heights = [30, 45, 60, 75, 90, 80];
    return heights[index] || 0;
  }

  // Helper method to get expenses bar height for line chart
  getExpensesBarHeight(index: number): number {
    // Sample data - in a real app, this would come from the backend
    const heights = [20, 30, 40, 50, 60, 45];
    return heights[index] || 0;
  }

  // Math object for template
  Math = Math;

  // Helper method to get top projects for evolution chart
  getTopProjectsForEvolution(): any[] {
    if (!this.dashboardData || !this.dashboardData.topProjects) {
      return [];
    }

    // Return top 3 projects
    return this.dashboardData.topProjects.slice(0, 3);
  }

  // Helper method to get project evolution height
  getProjectEvolutionHeight(projectIndex: number, monthIndex: number): number {
    const value = this.getProjectEvolutionValue(projectIndex, monthIndex);
    // Scale the value to a reasonable height (max 50px)
    return Math.min(Math.round(value / 200), 50);
  }

  // Helper method to get project evolution value
  getProjectEvolutionValue(projectIndex: number, monthIndex: number): number {
    // Sample data - in a real app, this would come from the backend
    const projectData = [
      [1000, 1500, 2200, 3000, 4500, 5000], // Project 1
      [2000, 2300, 2100, 2800, 3200, 3500], // Project 2
      [500, 800, 1200, 1800, 2500, 3000]    // Project 3
    ];

    if (projectIndex < projectData.length && monthIndex < projectData[projectIndex].length) {
      return projectData[projectIndex][monthIndex];
    }

    return 0;
  }

  // Helper method to get project color
  getProjectColor(index: number): string {
    const colors = [
      '#28a745', // Green
      '#17a2b8', // Cyan
      '#c9b037', // Gold
      '#fd7e14', // Orange
      '#6c757d'  // Gray
    ];

    return colors[index % colors.length];
  }

  // Helper method to calculate project funding percentage for bar chart
  getProjectFundingPercentage(fundsRaised: number): number {
    if (!this.dashboardData || !this.dashboardData.topProjects || this.dashboardData.topProjects.length === 0) {
      return 0;
    }

    // Find the maximum funds raised among all projects to use as the 100% mark
    const maxFunds = Math.max(...this.dashboardData.topProjects.map(p => p.fundsRaised));

    if (maxFunds <= 0) {
      return 0;
    }

    // Calculate percentage based on the maximum funds
    return Math.round((fundsRaised / maxFunds) * 100);
  }

  // Helper method to get project trend (for financial evolution)
  getProjectTrend(projectIndex: number): number {
    // Sample data - in a real app, this would come from the backend
    const trends = [15, -5, 10, 8, -3];
    return trends[projectIndex] || 0;
  }

  // No additional methods needed
}
