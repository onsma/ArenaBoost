import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { RouterModule } from '@angular/router';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { HomeComponent } from './pages/home/home.component';
import { ProjectsComponent } from './pages/projects/projects.component';
import { AuthComponent } from './pages/auth/auth.component';
import { RiskService } from './risk-management-frontend/risk/services/risk.service';
import { ServicesComponent } from './pages/services/services.component';
import { HowItWorksComponent } from './pages/how-it-works/how-it-works.component';
import { LoanRequestComponent } from './loan-request/loan-request.component';
import { LoanSimulationComponent } from './loan-simulation/loan-simulation.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    ProjectsComponent,
    AuthComponent,
    ServicesComponent,
    HowItWorksComponent,
    LoanRequestComponent,
    LoanSimulationComponent,
    DashboardComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    ReactiveFormsModule,
    RouterModule,
    AppRoutingModule
  ],
  providers: [RiskService],
  bootstrap: [AppComponent]
})
export class AppModule { }
