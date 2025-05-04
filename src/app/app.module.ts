import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { provideCharts, withDefaultRegisterables } from 'ng2-charts';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
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
import { SidebarLayoutComponent } from './components/sidebar-layout/sidebar-layout.component';
import { LoansComponent } from './pages/loans/loans.component';
import { ImageUploadComponent } from './components/image-upload/image-upload.component';
import { ImageTestComponent } from './components/image-test/image-test.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    ServicesComponent,
    ProjectsComponent,
    ProjectDetailComponent,
    ProjectFormComponent,
    ContributionFormComponent,
    AuthComponent,
    HowItWorksComponent,
    DashboardComponent,
    DashboardTestComponent,
    SidebarLayoutComponent,
    LoansComponent,
    ImageUploadComponent,
    ImageTestComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    provideCharts(withDefaultRegisterables())
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
