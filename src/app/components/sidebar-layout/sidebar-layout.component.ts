import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sidebar-layout',
  templateUrl: './sidebar-layout.component.html',
  styleUrls: ['./sidebar-layout.component.scss']
})
export class SidebarLayoutComponent {
  constructor(private router: Router) {}

  /**
   * Check if the current route matches the given path
   * @param path The path to check
   * @returns True if the current route starts with the given path
   */
  isActive(path: string): boolean {
    return this.router.url.startsWith(path);
  }
}
