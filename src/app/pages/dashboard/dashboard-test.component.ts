import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard-test',
  template: `
    <div class="container mt-5">
      <div class="card">
        <div class="card-header">
          <h2>Dashboard Test Page</h2>
        </div>
        <div class="card-body">
          <p class="lead">If you can see this page, the dashboard routing is working correctly.</p>
          <p>This is a simple test page to verify that the routing to the dashboard is functioning.</p>
          <a routerLink="/" class="btn btn-primary">Go Home</a>
        </div>
      </div>
    </div>
  `
})
export class DashboardTestComponent {}
