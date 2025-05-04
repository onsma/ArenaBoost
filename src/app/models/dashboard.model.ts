export interface DashboardMetrics {
  totalProjects: number;
  fundsRaised: number;
  successRate: number;
  activeProjects: number;
}

export interface ProjectProgress {
  projectId: number;
  projectName: string;
  currentAmount: number;
  goalAmount: number;
  progressPercentage: number;
}

export interface TopProject {
  projectId: number;
  projectName: string;
  fundsRaised: number;
  successProbability: number;
  daysRemaining: number;
}

export interface UrgentProject {
  projectId: number;
  projectName: string;
  daysRemaining: number;
  currentAmount: number;
  goalAmount: number;
  progressPercentage: number;
}

export interface FinancialSnapshot {
  totalFunds: number;
  totalExpenses: number;
  netFunds: number;
  fundsByCategory: {
    category: string;
    amount: number;
  }[];
}

export interface ProjectRecommendation {
  projectId: number;
  projectName: string;
  matchScore: number;
  category: string;
  description: string;
  imageUrl: string;
}

export interface UserContribution {
  projectId: number;
  projectName: string;
  amount: number;
  date: Date;
}

export interface DashboardData {
  metrics: DashboardMetrics;
  projectProgress: ProjectProgress[];
  topProjects: TopProject[];
  urgentProjects: UrgentProject[];
  financialSnapshot: FinancialSnapshot;
  recommendations: ProjectRecommendation[];
  userContributions: UserContribution[];
}
