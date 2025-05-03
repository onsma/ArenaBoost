import { Project } from './project.model';

export interface Contribution {
  id_contribution?: number;
  amount: number;
  supporter_name: string;
  reward: string;
  email: string;
  project?: Project;
}
