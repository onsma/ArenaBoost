export interface Project {
  id?: number;
  id_project?: number; // Keep for backward compatibility
  name: string;
  description: string;
  category: string;
  goal_amount: number;
  current_amount: number;
  total_expenses: number;
  duration_days: number;
  days_remaining: number;
  start_date: Date | string;
  end_date?: Date | string;
  status?: string; // 'ACTIVE', 'COMPLETED', 'CANCELLED'
  image?: string; // Matches the backend field name
  imageUrl?: string; // Keep for backward compatibility
  progressPercentage?: number; // Added for dashboard calculations
}

export enum Category {
  Equipement = 'Equipement',
  Tournament = 'Tournament',
  Formation = 'Formation'
}
