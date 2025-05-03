export interface Project {
  id_project?: number;
  name: string;
  description: string;
  category: Category;
  goal_amount: number;
  current_amount: number;
  total_expenses: number;
  duration_days: number;
  days_remaining: number;
  start_date: Date;
  image?: string; // Matches the backend field name
  imageUrl?: string; // Keep for backward compatibility
}

export enum Category {
  Equipement = 'Equipement',
  Tournament = 'Tournament',
  Formation = 'Formation'
}
