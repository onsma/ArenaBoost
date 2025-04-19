import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// Import components
import { HomeComponent } from './pages/home/home.component';
import { ServicesComponent } from './pages/services/services.component';
import { ProjectsComponent } from './pages/projects/projects.component';
import { AuthComponent } from './pages/auth/auth.component';
import { HowItWorksComponent } from './pages/how-it-works/how-it-works.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'services', component: ServicesComponent },
  { path: 'projects', component: ProjectsComponent },
  { path: 'how-it-works', component: HowItWorksComponent },
  { path: 'auth', component: AuthComponent },
  { path: '**', redirectTo: '' } // Redirect to home for any unknown routes
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
