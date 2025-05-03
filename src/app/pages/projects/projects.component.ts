import { Component, OnInit } from '@angular/core';
import { Project, Category } from '../../models/project.model';
import { ProjectService } from '../../services/project.service';

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.scss']
})
export class ProjectsComponent implements OnInit {
  projects: Project[] = [];
  categories = Object.values(Category);
  loading = true;
  error = false;
  selectedCategory: string = '';

  constructor(private projectService: ProjectService) {}

  ngOnInit(): void {
    this.loadProjects();
  }

  loadProjects(): void {
    this.loading = true;
    this.projectService.getAllProjects().subscribe({
      next: (data) => {
        this.projects = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading projects', err);
        this.error = true;
        this.loading = false;
      }
    });
  }

  filterByCategory(category: string): void {
    this.selectedCategory = category;
    if (!category) {
      this.loadProjects();
      return;
    }

    this.loading = true;
    this.projectService.getProjectsByCategory(category).subscribe({
      next: (data) => {
        this.projects = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error filtering projects', err);
        this.error = true;
        this.loading = false;
      }
    });
  }

  getProgressPercentage(project: Project): number {
    return Math.min(Math.round((project.current_amount / project.goal_amount) * 100), 100);
  }


}
