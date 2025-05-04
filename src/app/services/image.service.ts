import { Injectable } from '@angular/core';

export interface ProjectImage {
  id: string;
  name: string;
  path: string;
  category: string;
}

@Injectable({
  providedIn: 'root'
})
export class ImageService {
  // List of available images from external sources with categories
  private availableImages: ProjectImage[] = [
    // Tournament Category
    {
      id: 'football-tournament',
      name: 'Football Tournament',
      path: 'assets/images/projects/football-tournaament.jpg',
      category: 'Tournament'
    },
    {
      id: 'soccer-tournament',
      name: 'Soccer Tournament',
      path: 'assets/images/OIP.jpg',
      category: 'Tournament'
    },
    {
      id: 'basketball-tournament',
      name: 'Basketball Tournament',
      path: 'https://img.freepik.com/free-photo/sports-tools_53876-138077.jpg',
      category: 'Tournament'
    },

    // Equipment Category
    {
      id: 'sports-equipment',
      name: 'Sports Equipment',
      path: 'https://img.freepik.com/free-photo/top-view-sports-equipment_23-2148523277.jpg',
      category: 'Equipement'
    },
    {
      id: 'football-stadium',
      name: 'Football Stadium',
      path: 'https://img.freepik.com/free-photo/stadium-night_1150-14581.jpg',
      category: 'Equipement'
    },
    {
      id: 'swimming-facility',
      name: 'Swimming Facility',
      path: 'https://img.freepik.com/free-photo/swimming-pool-beach-luxury-hotel-outdoor-pools-spa-amara-dolce-vita-luxury-hotel-resort-tekirova-kemer-turkey_146671-18751.jpg',
      category: 'Equipement'
    },

    // Formation Category
    {
      id: 'coach-training',
      name: 'Coach Training',
      path: 'https://img.freepik.com/free-photo/coach-explaining-game-strategy-his-team_23-2149758138.jpg',
      category: 'Formation'
    },
    {
      id: 'team-training',
      name: 'Team Training',
      path: 'assets/images/pexels-szafran-16627321.jpg',
      category: 'Formation'
    }
  ];

  constructor() { }

  /**
   * Get all available images
   */
  getAvailableImages(): ProjectImage[] {
    return this.availableImages;
  }

  /**
   * Get all image categories
   */
  getImageCategories(): string[] {
    const categories = new Set(this.availableImages.map(img => img.category));
    return Array.from(categories);
  }

  /**
   * Get images by category
   */
  getImagesByCategory(category: string): ProjectImage[] {
    return this.availableImages.filter(img => img.category === category);
  }

  /**
   * Get image path by name
   */
  getImagePathByName(name: string): string | undefined {
    const image = this.availableImages.find(img => img.name === name);
    return image?.path;
  }

  /**
   * Get image by ID
   */
  getImageById(id: string): ProjectImage | undefined {
    return this.availableImages.find(img => img.id === id);
  }

  /**
   * Add a new image to the available images list
   * Note: This doesn't actually upload the image, just adds it to the list
   */
  addImage(name: string, path: string, category: string = 'Other'): void {
    // Check if the image already exists
    const exists = this.availableImages.some(img => img.path === path);
    if (!exists) {
      const id = name.toLowerCase().replace(/\s+/g, '-');
      this.availableImages.push({ id, name, path, category });
    }
  }
}
