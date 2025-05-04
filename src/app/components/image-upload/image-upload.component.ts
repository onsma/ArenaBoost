import { Component, EventEmitter, Output } from '@angular/core';
import { ImageService } from '../../services/image.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-image-upload',
  templateUrl: './image-upload.component.html',
  styleUrls: ['./image-upload.component.scss']
})
export class ImageUploadComponent {
  @Output() imageSelected = new EventEmitter<string>();

  uploadForm: FormGroup;
  selectedFile: File | null = null;
  previewUrl: string | null = null;
  loading = false;
  error = false;
  success = false;
  message = '';
  categories: string[] = [];

  constructor(
    private imageService: ImageService,
    private fb: FormBuilder
  ) {
    this.uploadForm = this.createForm();
    this.loadCategories();
  }

  createForm(): FormGroup {
    return this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      category: ['', Validators.required]
    });
  }

  loadCategories(): void {
    this.categories = this.imageService.getImageCategories();

    // Set default category if available
    if (this.categories.length > 0) {
      this.uploadForm.patchValue({
        category: this.categories[0]
      });
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;

    this.selectedFile = input.files[0];
    this.createPreview();

    // Set default name from filename
    const fileName = this.selectedFile.name.split('.')[0];
    const formattedName = fileName
      .replace(/[_-]/g, ' ')
      .replace(/\b\w/g, l => l.toUpperCase());

    this.uploadForm.patchValue({
      name: formattedName
    });
  }

  createPreview(): void {
    if (!this.selectedFile) return;

    const reader = new FileReader();
    reader.onload = () => {
      this.previewUrl = reader.result as string;
    };
    reader.readAsDataURL(this.selectedFile);
  }

  uploadImage(): void {
    if (this.uploadForm.invalid || !this.selectedFile) {
      this.error = true;
      this.message = 'Please fill all required fields and select an image';
      return;
    }

    this.loading = true;
    this.error = false;
    this.success = false;

    const formValue = this.uploadForm.value;

    // In a real application, you would upload the file to the server here
    // For this demo, we'll simulate the upload and just add the image to our service

    // Simulate server upload delay
    setTimeout(() => {
      try {
        // Generate a unique filename
        const timestamp = new Date().getTime();
        const filename = `${timestamp}_${this.selectedFile!.name}`;
        const path = `assets/images/projects/${filename}`;

        // Add the image to our service
        this.imageService.addImage(formValue.name, path, formValue.category);

        // Emit the image path
        this.imageSelected.emit(path);

        // Show success message
        this.success = true;
        this.message = 'Image uploaded successfully!';
        this.loading = false;

        // Reset the form
        this.reset();
      } catch (err) {
        this.error = true;
        this.message = 'Failed to upload image. Please try again.';
        this.loading = false;
      }
    }, 1500);
  }

  reset(): void {
    this.uploadForm.reset();
    this.selectedFile = null;
    this.previewUrl = null;
    this.error = false;
    this.success = false;
    this.message = '';

    // Reset category to default
    if (this.categories.length > 0) {
      this.uploadForm.patchValue({
        category: this.categories[0]
      });
    }
  }
}
