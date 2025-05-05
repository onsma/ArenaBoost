import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { LoanService } from './services/loan.service';
import { LoanTypeService } from './services/loantype.service';
import { DashboardService } from './services/dashboard.service';
import { RouterModule } from '@angular/router';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { HomeComponent } from './pages/home/home.component';
import { ServicesComponent } from './pages/services/services.component';
import { ProjectsComponent } from './pages/projects/projects.component';
import { AuthComponent } from './pages/auth/auth.component';
import { HowItWorksComponent } from './pages/how-it-works/how-it-works.component';
import { LoanRequestComponent } from './loan-request/loan-request.component';
import { LoanSimulationComponent } from './loan-simulation/loan-simulation.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { LoanListComponent } from './pages/loan-list/loan-list.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    ServicesComponent,
    ProjectsComponent,
    AuthComponent,
    HowItWorksComponent,
    LoanRequestComponent,
    LoanSimulationComponent,
    DashboardComponent,
    LoanListComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule
  ],
  providers: [
    LoanService,
    LoanTypeService,
    DashboardService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
