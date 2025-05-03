import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProjectService } from '../../services/project.service';
import { Project } from '../../models/project.model';
import { Contribution } from '../../models/contribution.model';

@Component({
  selector: 'app-contribution-form',
  templateUrl: './contribution-form.component.html',
  styleUrls: ['./contribution-form.component.scss']
})
export class ContributionFormComponent implements OnInit {
  contributionForm: FormGroup;
  project: Project | null = null;
  projectId: number | null = null;
  loading = false;
  loadingProject = true;
  submitted = false;
  error = false;
  errorMessage = '';
  success = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private projectService: ProjectService
  ) {
    this.contributionForm = this.createForm();
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.projectId = Number(id);
        this.loadProject(this.projectId);
      } else {
        this.error = true;
        this.errorMessage = 'Invalid project ID';
        this.loadingProject = false;
      }
    });
  }

  createForm(): FormGroup {
    return this.fb.group({
      amount: [100, [Validators.required, Validators.min(10)]],
      supporter_name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      reward: ['Thank you message']
    });
  }

  loadProject(id: number): void {
    this.loadingProject = true;
    this.projectService.getProjectById(id).subscribe({
      next: (data) => {
        this.project = data;
        this.loadingProject = false;
      },
      error: (err) => {
        console.error('Error loading project', err);
        this.error = true;
        this.errorMessage = 'Failed to load project details';
        this.loadingProject = false;
      }
    });
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.contributionForm.invalid || !this.projectId) {
      return;
    }

    this.loading = true;
    this.error = false;
    this.success = false;

    const formData = this.contributionForm.value;
    const contribution: Contribution = {
      amount: formData.amount,
      supporter_name: formData.supporter_name,
      email: formData.email,
      reward: formData.reward
    };

    // Call the service to add the contribution
    this.projectService.addContribution(this.projectId, contribution).subscribe({
      next: (result) => {
        console.log('Contribution added successfully:', result);
        this.loading = false;
        this.success = true;

        // Update the project's current amount
        if (this.project) {
          this.project.current_amount += contribution.amount;
        }

        // Reset form after successful submission
        this.contributionForm.reset({
          amount: 100,
          supporter_name: '',
          email: '',
          reward: 'Thank you message'
        });
        this.submitted = false;
      },
      error: (err) => {
        console.error('Error adding contribution:', err);
        this.error = true;
        this.errorMessage = 'Failed to process your contribution. Please try again later.';
        this.loading = false;
      }
    });
  }

  navigateToProject(): void {
    if (this.projectId) {
      this.router.navigate(['/projects', this.projectId]);
    } else {
      this.router.navigate(['/projects']);
    }
  }

  // Helper methods for form validation
  get f() { return this.contributionForm.controls; }

  hasError(controlName: string, errorName: string): boolean {
    const control = this.contributionForm.get(controlName);
    return !!(control && control.hasError(errorName) && (control.dirty || control.touched || this.submitted));
  }


}
