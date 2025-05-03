import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProjectService } from '../../services/project.service';
import { Project, Category } from '../../models/project.model';

@Component({
  selector: 'app-project-form',
  templateUrl: './project-form.component.html',
  styleUrls: ['./project-form.component.scss']
})
export class ProjectFormComponent implements OnInit {
  projectForm: FormGroup;
  isEditMode = false;
  projectId: number | null = null;
  loading = false;
  submitted = false;
  error = false;
  errorMessage = '';
  categories = Object.values(Category);

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private projectService: ProjectService
  ) {
    this.projectForm = this.createForm();
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.isEditMode = true;
        this.projectId = Number(id);
        this.loadProject(this.projectId);
      }
    });
  }

  createForm(): FormGroup {
    return this.fb.group({
      name: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.minLength(20)]],
      category: [Category.Tournament, Validators.required],
      goal_amount: [0, [Validators.required, Validators.min(1000)]],
      duration_days: [30, [Validators.required, Validators.min(1), Validators.max(365)]],
      image: ['']
    });
  }

  loadProject(id: number): void {
    this.loading = true;
    this.projectService.getProjectById(id).subscribe({
      next: (project) => {
        this.projectForm.patchValue({
          name: project.name,
          description: project.description,
          category: project.category,
          goal_amount: project.goal_amount,
          duration_days: project.duration_days,
          image: project.image || project.imageUrl
        });
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading project', err);
        this.error = true;
        this.errorMessage = 'Failed to load project details';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.projectForm.invalid) {
      return;
    }

    this.loading = true;

    const formData = this.projectForm.value;
    const project: Project = {
      name: formData.name,
      description: formData.description,
      category: formData.category,
      goal_amount: formData.goal_amount,
      current_amount: 0, // New projects start with 0 funding
      total_expenses: 0, // New projects start with 0 expenses
      duration_days: formData.duration_days,
      days_remaining: formData.duration_days, // Initially, days_remaining equals duration_days
      start_date: new Date(),
      image: formData.image
    };

    if (this.isEditMode && this.projectId) {
      // Update existing project
      project.id_project = this.projectId;
      this.projectService.updateProject(project).subscribe({
        next: (updatedProject) => {
          this.loading = false;
          this.router.navigate(['/projects', updatedProject.id_project]);
        },
        error: (err) => {
          console.error('Error updating project', err);
          this.error = true;
          this.errorMessage = 'Failed to update project';
          this.loading = false;
        }
      });
    } else {
      // Create new project
      this.projectService.createProject(project).subscribe({
        next: (newProject) => {
          this.loading = false;
          this.router.navigate(['/projects', newProject.id_project]);
        },
        error: (err) => {
          console.error('Error creating project', err);
          this.error = true;
          this.errorMessage = 'Failed to create project';
          this.loading = false;
        }
      });
    }
  }

  // Helper methods for form validation
  get f() { return this.projectForm.controls; }

  hasError(controlName: string, errorName: string): boolean {
    const control = this.projectForm.get(controlName);
    return !!(control && control.hasError(errorName) && (control.dirty || control.touched || this.submitted));
  }
}
