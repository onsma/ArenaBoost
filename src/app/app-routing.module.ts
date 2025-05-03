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

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'services', component: ServicesComponent },
  { path: 'projects', component: ProjectsComponent },
  { path: 'projects/create', component: ProjectFormComponent },
  { path: 'projects/:id', component: ProjectDetailComponent },
  { path: 'projects/:id/edit', component: ProjectFormComponent },
  { path: 'projects/:id/contribute', component: ContributionFormComponent },
  { path: 'how-it-works', component: HowItWorksComponent },
  { path: 'auth', component: AuthComponent },
  { path: '**', redirectTo: '' } // Redirect to home for any unknown routes
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
