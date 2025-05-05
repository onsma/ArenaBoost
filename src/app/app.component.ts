import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd, Event } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'arenaboost';
  currentRoute: string = '';

  constructor(private router: Router) {
    // Initialize the current route
    this.currentRoute = this.router.url;
    console.log('Initial route:', this.currentRoute);
  }

  ngOnInit() {
    // Subscribe to router events to update the current route
    this.router.events.pipe(
      filter((event: Event): event is NavigationEnd => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.currentRoute = event.urlAfterRedirects;
      console.log('Route changed to:', this.currentRoute);
    });
  }

  /**
   * Check if the current route is the dashboard route
   * @returns True if the current route is the dashboard route
   */
  isDashboardRoute(): boolean {
    const isDashboard = this.currentRoute === '/dashboard' || this.currentRoute.startsWith('/dashboard/');
    console.log('isDashboardRoute check:', this.currentRoute, isDashboard);
    return isDashboard;
  }
}
