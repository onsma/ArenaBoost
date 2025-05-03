import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// Import components
import { HomeComponent } from './pages/home/home.component';
import { ServicesComponent } from './pages/services/services.component';
import { ProjectsComponent } from './pages/projects/projects.component';
import { AuthComponent } from './pages/auth/auth.component';
import { HowItWorksComponent } from './pages/how-it-works/how-it-works.component';
import { LoanRequestComponent } from './loan-request/loan-request.component';
import { LoanSimulationComponent } from './loan-simulation/loan-simulation.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { LoanListComponent } from './pages/loan-list/loan-list.component';

const routes: Routes = [
  { path: 'dashboard', component: DashboardComponent },
  { path: '', component: HomeComponent },
  { path: 'services', component: ServicesComponent },
  { path: 'projects', component: ProjectsComponent },
  { path: 'how-it-works', component: HowItWorksComponent },
  { path: 'auth', component: AuthComponent },
  { path: 'loan-request', component: LoanRequestComponent },
  { path: 'loan-confirmation/:id', component: LoanRequestComponent },
  { path: 'loan-simulation', component: LoanSimulationComponent },
  { path: 'loan-list', component: LoanListComponent },
  { path: '**', redirectTo: '' } // Redirect to home for any unknown routes
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
