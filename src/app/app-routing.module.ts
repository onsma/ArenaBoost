import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// Import components
import { HomeComponent } from './pages/home/home.component';
import { ServicesComponent } from './pages/services/services.component';
import { ProjectsComponent } from './pages/projects/projects.component';
import { ProjectDetailComponent } from './pages/project-detail/project-detail.component';
import { ProjectFormComponent } from './pages/project-form/project-form.component';
import { ContributionFormComponent } from './pages/contribution-form/contribution-form.component';
import { AuthComponent } from './pages/auth/auth.component';
import { HowItWorksComponent } from './pages/how-it-works/how-it-works.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { DashboardTestComponent } from './pages/dashboard/dashboard-test.component';
import { LoansComponent } from './pages/loans/loans.component';
import { ImageTestComponent } from './components/image-test/image-test.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'dashboard-test', component: DashboardTestComponent },
  { path: 'services', component: ServicesComponent },
  { path: 'projects', component: ProjectsComponent },
  { path: 'projects/create', component: ProjectFormComponent },
  { path: 'projects/:id', component: ProjectDetailComponent },
  { path: 'projects/:id/edit', component: ProjectFormComponent },
  { path: 'projects/:id/contribute', component: ContributionFormComponent },
  { path: 'how-it-works', component: HowItWorksComponent },
  { path: 'auth', component: AuthComponent },
  { path: 'loans', component: LoansComponent },
  { path: 'image-test', component: ImageTestComponent },
  { path: '**', redirectTo: '' } // Redirect to home for any unknown routes
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
