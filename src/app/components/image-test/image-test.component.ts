import { Component } from '@angular/core';

@Component({
  selector: 'app-image-test',
  template: `
    <div class="container mt-5">
      <h2>Image Test</h2>
      <div class="row">
        <div class="col-md-6 mb-4" *ngFor="let image of images">
          <div class="card">
            <div class="card-header">
              {{ image.name }}
            </div>
            <div class="card-body">
              <img [src]="image.path" [alt]="image.name" class="img-fluid" style="height: 200px; object-fit: contain;">
              <p class="mt-2">Path: {{ image.path }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class ImageTestComponent {
  images = [
    { name: 'LOGO', path: '/assets/images/LOGO.png' },
    { name: 'Soccer Field', path: '/assets/images/soccer-field-night.jpg' },
    { name: 'Rocket Icon', path: '/assets/images/rocket-icon.png' },
    { name: 'Sports Photo', path: '/assets/images/pexels-szafran-16627321.jpg' },
    { name: 'User Avatar', path: '/assets/images/user-avatar.png' },
    { name: 'Test with assets', path: 'assets/images/LOGO.png' },
    { name: 'Test with relative path', path: './assets/images/LOGO.png' }
  ];
}
