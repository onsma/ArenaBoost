const routes: Routes = [
  { path: '', component: HomeComponent },
  { 
    path: 'dashboard', 
    component: DashboardComponent,
    children: [
      { path: 'loans', component: LoansComponent },
      { path: 'loans/new', component: LoanRequestComponent },
      { path: 'loans/:id', component: LoanDetailsComponent },
      { path: 'statistics', component: StatisticsComponent },
      { path: 'profile', component: ProfileComponent }
    ]
  },
  { path: 'auth/login', component: LoginComponent },
  { path: 'auth/register', component: RegisterComponent }
];