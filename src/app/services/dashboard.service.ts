import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, forkJoin, throwError } from 'rxjs';
import { catchError, map, tap, switchMap } from 'rxjs/operators';
import { DashboardData } from '../models/dashboard.model';
import { Project } from '../models/project.model';
import { ProjectAnalytics } from '../models/project-analytics.model';
import { ProjectPrediction } from '../models/project-prediction.model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = 'http://localhost:8089/project'; // Spring Boot backend URL
  private currentUser = 'defaultUser'; // This would normally come from an auth service

  constructor(private http: HttpClient) { }

  /**
   * Get dashboard data by combining multiple API calls to the Spring Boot backend
   */
  getDashboardData(): Observable<DashboardData> {
    console.log('Fetching dashboard data from backend...');

    // Get all projects from the real database
    return this.http.get<Project[]>(`${this.apiUrl}/retrieve-all-projects`).pipe(
      tap(projects => console.log(`Fetched ${projects.length} projects from backend`)),
      switchMap(projects => {
        if (!projects || projects.length === 0) {
          console.warn('No projects found in database');
          return of(this.createEmptyDashboardData());
        }

        // Create an array of observables for each project's analytics and prediction
        const analyticsRequests = projects.slice(0, 5).map(project =>
          this.http.get<any>(`${this.apiUrl}/${project.id}/analytics`).pipe(
            catchError(error => {
              console.warn(`Failed to fetch analytics for project ${project.id}:`, error);
              return of(null);
            })
          )
        );

        const progressRequests = projects.map(project =>
          this.http.get<number>(`${this.apiUrl}/${project.id}/progress`).pipe(
            catchError(error => {
              console.warn(`Failed to fetch progress for project ${project.id}:`, error);
              return of(0);
            })
          )
        );

        // Combine all requests
        return forkJoin({
          projects: of(projects),
          analytics: forkJoin(analyticsRequests.length > 0 ? analyticsRequests : [of(null)]),
          progress: forkJoin(progressRequests.length > 0 ? progressRequests : [of(0)])
        }).pipe(
          map(results => {
            console.log('All data fetched from backend:', results);
            return this.processRealDashboardData(
              results.projects,
              results.analytics.filter(a => a !== null),
              results.progress
            );
          })
        );
      }),
      catchError(error => {
        console.error('Error fetching dashboard data from backend:', error);
        return throwError(() => new Error('Failed to load dashboard data from backend'));
      })
    );
  }

  /**
   * Process real data from the backend to create the dashboard data
   */
  private processRealDashboardData(
    projects: Project[],
    analytics: any[],
    progressValues: number[]
  ): DashboardData {
    console.log('Processing real data for dashboard:', { projects, analytics, progressValues });

    if (!projects || projects.length === 0) {
      return this.createEmptyDashboardData();
    }

    // Calculate metrics
    const totalProjects = projects.length;
    const activeProjects = projects.filter(p => p.status === 'ACTIVE').length;
    const fundsRaised = projects.reduce((sum, p) => sum + (p.current_amount || 0), 0);
    const successRate = this.calculateSuccessRate(projects);

    // Assign progress values to projects
    projects.forEach((project, index) => {
      if (index < progressValues.length) {
        project.progressPercentage = progressValues[index];
      } else {
        project.progressPercentage = this.calculateProgressPercentage(project);
      }
    });

    // Sort projects by funds raised (descending) for top projects
    const sortedByFunds = [...projects].sort((a, b) =>
      (b.current_amount || 0) - (a.current_amount || 0)
    );

    // Get top 5 projects
    const topProjects = sortedByFunds.slice(0, 5).map(p => ({
      projectId: p.id || 0,
      projectName: p.name,
      fundsRaised: p.current_amount || 0,
      successProbability: this.calculateSuccessProbability(p),
      daysRemaining: this.calculateDaysRemaining(p)
    }));

    // Calculate project progress for all projects
    const projectProgress = projects.map((p, index) => ({
      projectId: p.id || 0,
      projectName: p.name,
      currentAmount: p.current_amount || 0,
      goalAmount: p.goal_amount || 0,
      progressPercentage: p.progressPercentage || this.calculateProgressPercentage(p)
    }));

    // Get urgent projects (projects with less than 15 days remaining)
    const urgentProjects = projects
      .filter(p => this.calculateDaysRemaining(p) <= 15 && p.status !== 'COMPLETED')
      .map(p => ({
        projectId: p.id || 0,
        projectName: p.name,
        daysRemaining: this.calculateDaysRemaining(p),
        currentAmount: p.current_amount || 0,
        goalAmount: p.goal_amount || 0,
        progressPercentage: p.progressPercentage || this.calculateProgressPercentage(p)
      }))
      .sort((a, b) => a.daysRemaining - b.daysRemaining)
      .slice(0, 3); // Get top 3 urgent projects

    // Calculate financial snapshot
    const totalExpenses = projects.reduce((sum, p) => sum + (p.total_expenses || 0), 0);
    const netFunds = fundsRaised - totalExpenses;

    // Group projects by category for financial breakdown
    const categoriesMap = new Map<string, number>();
    projects.forEach(p => {
      const category = p.category || 'Other';
      const currentAmount = categoriesMap.get(category) || 0;
      categoriesMap.set(category, currentAmount + (p.current_amount || 0));
    });

    const fundsByCategory = Array.from(categoriesMap.entries()).map(([category, amount]) => ({
      category,
      amount
    }));

    // Try to get recommendations from the backend
    // For now, just use the top projects that aren't in the urgent list
    const recommendationProjects = projects
      .filter(p => !urgentProjects.some(up => up.projectId === p.id))
      .sort(() => 0.5 - Math.random())
      .slice(0, 3);

    const recommendations = recommendationProjects.map(p => ({
      projectId: p.id || 0,
      projectName: p.name,
      matchScore: Math.floor(Math.random() * 30) + 70, // Random match score between 70-100%
      category: p.category || 'Other',
      description: p.description || 'No description available',
      imageUrl: p.image || '/assets/images/soccer-field-night.jpg'
    }));

    // Generate user contributions based on real projects
    // In a real app, we would get this from the backend
    const userContributions = this.generateUserContributions(projects);

    return {
      metrics: {
        totalProjects,
        activeProjects,
        fundsRaised,
        successRate
      },
      topProjects,
      projectProgress,
      urgentProjects,
      financialSnapshot: {
        totalFunds: fundsRaised,
        totalExpenses,
        netFunds,
        fundsByCategory
      },
      recommendations,
      userContributions
    };
  }

  /**
   * Process the projects data to create the dashboard data structure
   */
  private processDashboardData(projects: Project[]): DashboardData {
    if (!projects || projects.length === 0) {
      console.warn('No projects found, returning empty dashboard data');
      return this.createEmptyDashboardData();
    }

    console.log('Processing projects for dashboard data');

    // Calculate metrics
    const totalProjects = projects.length;
    const activeProjects = projects.filter(p => p.status === 'ACTIVE').length;
    const fundsRaised = projects.reduce((sum, p) => sum + (p.current_amount || 0), 0);
    const successRate = this.calculateSuccessRate(projects);

    // Sort projects by funds raised (descending) for top projects
    const sortedByFunds = [...projects].sort((a, b) =>
      (b.current_amount || 0) - (a.current_amount || 0)
    );

    // Get top 5 projects
    const topProjects = sortedByFunds.slice(0, 5).map(p => ({
      projectId: p.id || 0, // Default to 0 if id is undefined
      projectName: p.name,
      fundsRaised: p.current_amount || 0,
      successProbability: this.calculateSuccessProbability(p),
      daysRemaining: this.calculateDaysRemaining(p)
    }));

    // Calculate project progress for all projects
    const projectProgress = projects.map(p => ({
      projectId: p.id || 0, // Default to 0 if id is undefined
      projectName: p.name,
      currentAmount: p.current_amount || 0,
      goalAmount: p.goal_amount || 0,
      progressPercentage: this.calculateProgressPercentage(p)
    }));

    // Get urgent projects (projects with less than 15 days remaining)
    const urgentProjects = projects
      .filter(p => this.calculateDaysRemaining(p) <= 15 && p.status === 'ACTIVE')
      .map(p => ({
        projectId: p.id || 0, // Default to 0 if id is undefined
        projectName: p.name,
        daysRemaining: this.calculateDaysRemaining(p),
        currentAmount: p.current_amount || 0,
        goalAmount: p.goal_amount || 0,
        progressPercentage: this.calculateProgressPercentage(p)
      }))
      .sort((a, b) => a.daysRemaining - b.daysRemaining)
      .slice(0, 3); // Get top 3 urgent projects

    // Calculate financial snapshot
    const totalExpenses = projects.reduce((sum, p) => sum + (p.total_expenses || 0), 0);
    const netFunds = fundsRaised - totalExpenses;

    // Group projects by category for financial breakdown
    const categoriesMap = new Map<string, number>();
    projects.forEach(p => {
      const category = p.category || 'Other';
      const currentAmount = categoriesMap.get(category) || 0;
      categoriesMap.set(category, currentAmount + (p.current_amount || 0));
    });

    const fundsByCategory = Array.from(categoriesMap.entries()).map(([category, amount]) => ({
      category,
      amount
    }));

    // Generate recommendations based on project categories and popularity
    const recommendations = this.generateRecommendations(projects).map(p => ({
      projectId: p.id || 0, // Default to 0 if id is undefined
      projectName: p.name,
      matchScore: Math.floor(Math.random() * 30) + 70, // Random match score between 70-100%
      category: p.category || 'Other',
      description: p.description || 'No description available',
      imageUrl: p.image || '/assets/images/soccer-field-night.jpg'
    }));

    // Generate mock user contributions (in a real app, this would come from the backend)
    const userContributions = this.generateMockUserContributions(projects);

    return {
      metrics: {
        totalProjects,
        activeProjects,
        fundsRaised,
        successRate
      },
      topProjects,
      projectProgress,
      urgentProjects,
      financialSnapshot: {
        totalFunds: fundsRaised,
        totalExpenses,
        netFunds,
        fundsByCategory
      },
      recommendations,
      userContributions
    };
  }

  /**
   * Create empty dashboard data structure
   */
  private createEmptyDashboardData(): DashboardData {
    return {
      metrics: {
        totalProjects: 0,
        activeProjects: 0,
        fundsRaised: 0,
        successRate: 0
      },
      topProjects: [],
      projectProgress: [],
      urgentProjects: [],
      financialSnapshot: {
        totalFunds: 0,
        totalExpenses: 0,
        netFunds: 0,
        fundsByCategory: []
      },
      recommendations: [],
      userContributions: []
    };
  }

  /**
   * Calculate success rate based on project progress and time remaining
   */
  private calculateSuccessRate(projects: Project[]): number {
    if (!projects || projects.length === 0) return 0;

    const completedProjects = projects.filter(p => p.status === 'COMPLETED');
    if (completedProjects.length === 0) return 0;

    const successfulProjects = completedProjects.filter(p =>
      (p.current_amount || 0) >= (p.goal_amount || 0)
    );

    return Math.round((successfulProjects.length / completedProjects.length) * 100);
  }

  /**
   * Calculate success probability for a project
   */
  private calculateSuccessProbability(project: Project): number {
    const progressPercentage = this.calculateProgressPercentage(project);
    const daysRemaining = this.calculateDaysRemaining(project);

    // Simple formula: higher progress and more days remaining = higher probability
    let probability = progressPercentage * 0.7 + Math.min(daysRemaining, 30) * 0.3;

    // Cap at 100%
    return Math.min(Math.round(probability), 100);
  }

  /**
   * Calculate days remaining for a project
   */
  private calculateDaysRemaining(project: Project): number {
    if (!project.end_date) return 30; // Default to 30 days if no end date

    const endDate = new Date(project.end_date);
    const today = new Date();

    const diffTime = endDate.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    return Math.max(diffDays, 0); // Ensure we don't return negative days
  }

  /**
   * Calculate progress percentage for a project
   */
  private calculateProgressPercentage(project: Project): number {
    if (!project.goal_amount || project.goal_amount <= 0) return 0;

    const progress = ((project.current_amount || 0) / project.goal_amount) * 100;
    return Math.min(Math.round(progress), 100); // Cap at 100%
  }

  /**
   * Generate recommendations based on project categories and popularity
   */
  private generateRecommendations(projects: Project[]): Project[] {
    if (!projects || projects.length <= 3) return projects;

    // In a real app, we would call the backend recommendation endpoint
    // For now, just return some random projects
    const shuffled = [...projects].sort(() => 0.5 - Math.random());
    return shuffled.slice(0, 3);
  }

  /**
   * Generate mock user contributions
   */
  private generateMockUserContributions(projects: Project[]): any[] {
    if (!projects || projects.length === 0) return [];

    // In a real app, we would get this from the backend
    // For now, generate some mock contributions
    const shuffled = [...projects].sort(() => 0.5 - Math.random());
    const selectedProjects = shuffled.slice(0, Math.min(3, projects.length));

    return selectedProjects.map(p => ({
      projectId: p.id || 0, // Default to 0 if id is undefined
      projectName: p.name,
      amount: Math.floor(Math.random() * 500) + 100, // Random amount between 100-600
      date: new Date(Date.now() - Math.floor(Math.random() * 30) * 24 * 60 * 60 * 1000) // Random date in last 30 days
    }));
  }

  /**
   * Generate user contributions based on real projects
   */
  private generateUserContributions(projects: Project[]): any[] {
    if (!projects || projects.length === 0) return [];

    // In a real app, we would get this from the backend API
    // For now, generate some contributions based on real projects
    const activeProjects = projects.filter(p => p.status !== 'COMPLETED' && p.current_amount > 0);

    if (activeProjects.length === 0) {
      return this.generateMockUserContributions(projects);
    }

    const selectedProjects = activeProjects
      .sort((a, b) => (b.current_amount || 0) - (a.current_amount || 0))
      .slice(0, Math.min(3, activeProjects.length));

    return selectedProjects.map(p => {
      // Calculate a realistic contribution amount based on the project's current amount
      const maxContribution = Math.min(p.current_amount || 500, 1000);
      const minContribution = Math.max(50, maxContribution / 10);
      const amount = Math.floor(Math.random() * (maxContribution - minContribution)) + minContribution;

      // Generate a date in the last 30 days
      const date = new Date(Date.now() - Math.floor(Math.random() * 30) * 24 * 60 * 60 * 1000);

      return {
        projectId: p.id || 0,
        projectName: p.name,
        amount,
        date
      };
    });
  }

  /**
   * Get analytics for a specific project
   */
  getProjectAnalytics(projectId: number): Observable<ProjectAnalytics> {
    return this.http.get<ProjectAnalytics>(`${this.apiUrl}/${projectId}/analytics`).pipe(
      catchError(error => {
        console.error(`Error fetching analytics for project ${projectId}:`, error);
        return throwError(() => new Error('Failed to load project analytics'));
      })
    );
  }

  /**
   * Get prediction for a project outcome
   */
  getProjectPrediction(projectId: number): Observable<ProjectPrediction> {
    return this.http.get<ProjectPrediction>(`${this.apiUrl}/${projectId}/predict-outcome`).pipe(
      catchError(error => {
        console.error(`Error fetching prediction for project ${projectId}:`, error);
        return throwError(() => new Error('Failed to load project prediction'));
      })
    );
  }

  /**
   * Get recommendations for the current user
   */
  getRecommendations(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.apiUrl}/${this.currentUser}/recommendations`).pipe(
      catchError(error => {
        console.error('Error fetching recommendations:', error);
        return throwError(() => new Error('Failed to load recommendations'));
      })
    );
  }
}
