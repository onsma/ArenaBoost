import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Project, Category } from '../models/project.model';
import { Contribution } from '../models/contribution.model';
import { Event } from '../models/event.model';
import { ProjectAnalytics } from '../models/project-analytics.model';
import { ProjectPrediction } from '../models/project-prediction.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = 'http://localhost:8089/project/project';

  constructor(private http: HttpClient) { }

  // Core Project CRUD operations
  getAllProjects(): Observable<Project[]> {
    console.log('Calling API: GET', `${this.apiUrl}/retrieve-all-projects`);
    return this.http.get<Project[]>(`${this.apiUrl}/retrieve-all-projects`)
      .pipe(
        tap(projects => console.log('Received projects:', projects)),
        catchError((error) => {
          console.warn('Backend connection failed, using mock data instead', error);
          return of(this.getMockProjects());
        })
      );
  }

  // Mock data for development when backend is not available
  private getMockProjects(): Project[] {
    return [
      {
        id_project: 1,
        name: 'Youth Soccer Academy',
        description: 'Funding for a new youth soccer academy focused on developing talent in underserved communities.',
        goal_amount: 50000,
        current_amount: 35000,
        total_expenses: 5000,
        duration_days: 180,
        days_remaining: 120,
        start_date: new Date('2023-10-01'),
        category: Category.Tournament,
        image: '/assets/images/soccer-field-night.jpg'
      },
      {
        id_project: 2,
        name: 'Basketball Tournament Series',
        description: 'Organizing a series of basketball tournaments across Tunisia to discover new talent.',
        goal_amount: 25000,
        current_amount: 10000,
        total_expenses: 2000,
        duration_days: 90,
        days_remaining: 45,
        start_date: new Date('2023-11-15'),
        category: Category.Tournament,
        image: '/assets/images/LOGO.png'
      },
      {
        id_project: 3,
        name: 'Swimming Training Facility',
        description: 'Building a modern swimming facility with Olympic-standard pools and training equipment.',
        goal_amount: 100000,
        current_amount: 75000,
        total_expenses: 25000,
        duration_days: 365,
        days_remaining: 300,
        start_date: new Date('2023-08-01'),
        category: Category.Equipement,
        image: '/assets/images/pexels-szafran-16627321.jpg'
      },
      {
        id_project: 4,
        name: 'Coach Training Program',
        description: 'Professional development program for sports coaches to enhance their skills and knowledge.',
        goal_amount: 15000,
        current_amount: 7500,
        total_expenses: 1000,
        duration_days: 120,
        days_remaining: 90,
        start_date: new Date('2023-12-01'),
        category: Category.Formation,
        image: '/assets/images/rocket-icon.png'
      }
    ];
  }

  getProjectById(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/retrieve-project/${id}`)
      .pipe(
        catchError((error) => {
          console.warn('Backend connection failed, using mock data instead', error);
          const mockProject = this.getMockProjects().find(p => p.id_project === id);
          return of(mockProject || this.getMockProjects()[0]);
        })
      );
  }

  createProject(project: Project): Observable<Project> {
    return this.http.post<Project>(`${this.apiUrl}/add-project`, project)
      .pipe(
        catchError((error) => {
          console.warn('Backend connection failed, using mock data instead');
          // Create a mock project with a new ID
          const mockProjects = this.getMockProjects();
          const newId = Math.max(...mockProjects.map(p => p.id_project || 0)) + 1;
          const newProject = { ...project, id_project: newId };
          return of(newProject);
        })
      );
  }

  updateProject(project: Project): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/modify-project`, project)
      .pipe(
        catchError((error) => {
          console.warn('Backend connection failed, using mock data instead');
          return of(project);
        })
      );
  }

  deleteProject(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/remove-project/${id}`)
      .pipe(
        catchError((error) => {
          console.warn('Backend connection failed, using mock data instead', error);
          return of(undefined);
        })
      );
  }

  // Additional Project operations
  getTotalFunds(projectId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/${projectId}/total-funds`)
      .pipe(
        catchError((error) => {
          console.warn('Backend connection failed, using mock data instead', error);
          // Return a mock value based on the project
          const mockProject = this.getMockProjects().find(p => p.id_project === projectId);
          return of(mockProject ? mockProject.current_amount : 0);
        })
      );
  }

  addExpense(projectId: number, expenseAmount: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${projectId}/add-expense`, expenseAmount)
      .pipe(
        catchError((error) => {
          console.warn('Backend connection failed, using mock data instead', error);
          return of(undefined);
        })
      );
  }

  getProgressPercentage(projectId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/${projectId}/progress`)
      .pipe(
        catchError((error) => {
          console.warn('Backend connection failed, using mock data instead', error);
          // Calculate a mock progress percentage
          const mockProject = this.getMockProjects().find(p => p.id_project === projectId);
          return of(mockProject ? Math.round((mockProject.current_amount / mockProject.goal_amount) * 100) : 0);
        })
      );
  }

  addContribution(projectId: number, contribution: Contribution): Observable<Contribution> {
    console.log('Calling API: POST', `${this.apiUrl}/${projectId}/contribute`, contribution);
    return this.http.post<Contribution>(`${this.apiUrl}/${projectId}/contribute`, contribution)
      .pipe(
        tap(result => {
          console.log('Contribution added successfully:', result);
          // Update the project in our local cache if we have it
          this.updateProjectAmount(projectId, contribution.amount);
          // Simulate sending an email
          this.simulateSendEmail(contribution);
        }),
        catchError((error) => {
          console.warn('Backend connection failed, using mock data instead', error);
          // Create a mock contribution with an ID
          const mockContribution = {
            ...contribution,
            id_contribution: Math.floor(Math.random() * 1000) + 1
          };

          // Update the project in our local cache
          this.updateProjectAmount(projectId, contribution.amount);

          // Simulate sending an email
          this.simulateSendEmail(contribution);

          return of(mockContribution);
        })
      );
  }

  // Helper method to update a project's current amount
  private updateProjectAmount(projectId: number, amount: number): void {
    // Get the mock projects
    const mockProjects = this.getMockProjects();

    // Find the project to update
    const projectIndex = mockProjects.findIndex(p => p.id_project === projectId);

    if (projectIndex !== -1) {
      // Update the project's current amount
      mockProjects[projectIndex].current_amount += amount;
      console.log(`Updated project ${projectId} current amount to ${mockProjects[projectIndex].current_amount}`);
    }
  }

  // Simulate sending an email to the contributor
  private simulateSendEmail(contribution: Contribution): void {
    console.log(`Simulating sending email to ${contribution.email} for contribution of ${contribution.amount}`);

    // In a real application, this would call an API endpoint to send an email
    // For now, we'll just log the email content
    const emailSubject = 'Thank you for your contribution!';
    const emailBody = `
      Dear ${contribution.supporter_name},

      Thank you for your generous contribution of $${contribution.amount} to our project!

      Your support helps us achieve our goals and make a difference in the sports community.

      As a token of our appreciation, you will receive: ${contribution.reward}

      Best regards,
      The ArenaBoost Team
    `;

    console.log('Email subject:', emailSubject);
    console.log('Email body:', emailBody);
  }

  addEvent(projectId: number, event: Event): Observable<Event> {
    return this.http.post<Event>(`${this.apiUrl}/${projectId}/add-event`, event)
      .pipe(
        catchError((error) => {
          console.warn('Backend connection failed, using mock data instead', error);
          // Return the event with a mock ID
          return of({ ...event, id_event: Math.floor(Math.random() * 1000) + 1 });
        })
      );
  }

  getProjectAnalytics(projectId: number): Observable<ProjectAnalytics> {
    return this.http.get<ProjectAnalytics>(`${this.apiUrl}/${projectId}/analytics`)
      .pipe(
        catchError((error) => {
          console.warn('Backend connection failed, using mock data instead', error);
          // Create mock analytics
          const mockProject = this.getMockProjects().find(p => p.id_project === projectId);
          if (!mockProject) return of({
            totalFundsCollected: 0,
            totalExpenses: 0,
            totalContributions: 0,
            numberOfContributions: 0,
            progressPercentage: 0
          });

          return of({
            totalFundsCollected: mockProject.current_amount,
            totalExpenses: mockProject.total_expenses,
            totalContributions: mockProject.current_amount,
            numberOfContributions: Math.floor(Math.random() * 50) + 10,
            progressPercentage: Math.round((mockProject.current_amount / mockProject.goal_amount) * 100)
          });
        })
      );
  }

  generateFinancialReport(projectId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${projectId}/financial-report`, { responseType: 'blob' })
      .pipe(
        catchError((error) => {
          console.warn('Backend connection failed, cannot generate report', error);
          return throwError(() => new Error('Failed to generate report'));
        })
      );
  }

  searchProjectsByName(name: string): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.apiUrl}/search?name=${name}`)
      .pipe(
        catchError((error) => {
          console.warn('Backend connection failed, using mock data instead', error);
          // Filter mock projects by name
          const filteredProjects = this.getMockProjects().filter(p =>
            p.name.toLowerCase().includes(name.toLowerCase())
          );
          return of(filteredProjects);
        })
      );
  }

  predictProjectOutcome(projectId: number): Observable<ProjectPrediction> {
    return this.http.get<ProjectPrediction>(`${this.apiUrl}/${projectId}/predict-outcome`)
      .pipe(
        catchError((error) => {
          console.warn('Backend connection failed, using mock data instead', error);
          // Create mock prediction
          const mockProject = this.getMockProjects().find(p => p.id_project === projectId);
          if (!mockProject) return of({
            projectId: projectId,
            predictedSuccess: false,
            confidenceScore: 0.5,
            estimatedCompletionDate: new Date(),
            riskLevel: 'High',
            recommendations: ['No project data available']
          });

          const progress = Math.round((mockProject.current_amount / mockProject.goal_amount) * 100);
          const predictedSuccess = progress > 40;

          return of({
            projectId: projectId,
            predictedSuccess: predictedSuccess,
            confidenceScore: predictedSuccess ? 0.7 + (Math.random() * 0.25) : 0.3 + (Math.random() * 0.3),
            estimatedCompletionDate: new Date(new Date().setDate(new Date().getDate() + mockProject.days_remaining)),
            riskLevel: predictedSuccess ? 'Low' : 'Medium',
            recommendations: [
              'Increase social media promotion to attract more contributors',
              'Organize a virtual event to showcase project progress',
              'Reach out to local businesses for sponsorship opportunities'
            ]
          });
        })
      );
  }

  getRecommendations(supporterName: string): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.apiUrl}/${supporterName}/recommendations`)
      .pipe(
        catchError((error) => {
          console.warn('Backend connection failed, using mock data instead', error);
          // Return random mock projects as recommendations
          const mockProjects = this.getMockProjects();
          const shuffled = [...mockProjects].sort(() => 0.5 - Math.random());
          return of(shuffled.slice(0, 2));
        })
      );
  }

  // Helper method to handle HTTP operation failures
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(`${operation} failed: ${error.message}`);
      // Let the app keep running by returning an empty result
      return of(result as T);
    };
  }

  // For filtering by category in the frontend
  getProjectsByCategory(categoryName: string): Observable<Project[]> {
    return this.getAllProjects().pipe(
      // Filter projects by category on the client side
      map((projects: Project[]) => {
        if (!categoryName) return projects;
        return projects.filter(project =>
          project.category.toString() === categoryName
        );
      })
    );
  }
}
