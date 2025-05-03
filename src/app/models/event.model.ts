import { Project } from './project.model';

export interface Event {
  id_event?: number;
  name: string;
  ticket_price: number;
  tickets_sold: number;
  total_revenue: number;
  project?: Project;
}
