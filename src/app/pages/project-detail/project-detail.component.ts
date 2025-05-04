import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { ProjectService } from '../../services/project.service';
import { Project, Category } from '../../models/project.model';
import { ProjectAnalytics } from '../../models/project-analytics.model';
import { ProjectPrediction } from '../../models/project-prediction.model';
import { Subscription, filter } from 'rxjs';

@Component({
  selector: 'app-project-detail',
  templateUrl: './project-detail.component.html',
  styleUrls: ['./project-detail.component.scss']
})
export class ProjectDetailComponent implements OnInit, OnDestroy {
  project: Project | null = null;
  analytics: ProjectAnalytics | null = null;
  prediction: ProjectPrediction | null = null;
  loading = true;
  error = false;
  errorMessage = '';
  private routerSubscription: Subscription | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private projectService: ProjectService
  ) {}

  ngOnInit(): void {
    // Load the project initially
    this.route.paramMap.subscribe(params => {
      const projectId = Number(params.get('id'));
      if (projectId) {
        this.loadProject(projectId);
      } else {
        this.error = true;
        this.errorMessage = 'Invalid project ID';
        this.loading = false;
      }
    });

    // Subscribe to router events to refresh data when navigating back from contribution page
    this.routerSubscription = this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        const projectId = Number(this.route.snapshot.paramMap.get('id'));
        if (projectId) {
          console.log('Refreshing project data after navigation');
          this.loadProject(projectId);
        }
      });
  }

  ngOnDestroy(): void {
    // Clean up subscriptions
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
  }

  loadProject(id: number): void {
    this.loading = true;
    this.projectService.getProjectById(id).subscribe({
      next: (data) => {
        this.project = data;
        this.loadAnalytics(id);
      },
      error: (err) => {
        console.error('Error loading project', err);
        this.error = true;
        this.errorMessage = 'Failed to load project details';
        this.loading = false;
      }
    });
  }

  loadAnalytics(id: number): void {
    // Create mock analytics since we don't have a backend connection
    this.analytics = {
      totalFundsCollected: this.project?.current_amount || 0,
      totalExpenses: this.project?.total_expenses || 0,
      totalContributions: this.project?.current_amount || 0,
      numberOfContributions: Math.floor(Math.random() * 50) + 10, // Random number between 10 and 60
      progressPercentage: this.getProgressPercentage()
    };
    this.loadPrediction(id);
  }

  loadPrediction(id: number): void {
    // Create mock prediction since we don't have a backend connection
    const progress = this.getProgressPercentage();
    const predictedSuccess = progress > 40;
    const daysRemaining = this.project?.days_remaining || 30;

    this.prediction = {
      projectId: id,
      predictedSuccess: predictedSuccess,
      confidenceScore: predictedSuccess ? 0.7 + (Math.random() * 0.25) : 0.3 + (Math.random() * 0.3),
      estimatedCompletionDate: new Date(new Date().setDate(new Date().getDate() + daysRemaining)),
      riskLevel: predictedSuccess ? 'Low' : 'Medium',
      recommendations: [
        'Increase social media promotion to attract more contributors',
        'Organize a virtual event to showcase project progress',
        'Reach out to local businesses for sponsorship opportunities'
      ]
    };
    this.loading = false;
  }

  getProgressPercentage(): number {
    if (!this.project) return 0;
    return Math.min(Math.round((this.project.current_amount / this.project.goal_amount) * 100), 100);
  }

  downloadReport(): void {
    if (!this.project?.id_project) return;

    this.projectService.generateFinancialReport(this.project.id_project).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `project-report-${this.project?.id_project}.pdf`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        a.remove();
      },
      error: (err) => {
        console.error('Error downloading report', err);
        alert('Failed to download report. Please try again later.');
      }
    });
  }

  navigateToContribute(): void {
    if (this.project?.id_project) {
      this.router.navigate(['/projects', this.project.id_project, 'contribute']);
    }
  }

  navigateToEdit(): void {
    if (this.project?.id_project) {
      this.router.navigate(['/projects', this.project.id_project, 'edit']);
    }
  }

  deleteProject(): void {
    if (!this.project?.id_project) return;

    // Confirm before deleting
    if (confirm(`Are you sure you want to delete the project "${this.project.name}"? This action cannot be undone.`)) {
      this.loading = true;
      this.projectService.deleteProject(this.project.id_project).subscribe({
        next: () => {
          console.log('Project deleted successfully');
          this.router.navigate(['/projects']);
        },
        error: (err) => {
          console.error('Error deleting project', err);
          this.error = true;
          this.errorMessage = 'Failed to delete project. Please try again later.';
          this.loading = false;
        }
      });
    }
  }

}
