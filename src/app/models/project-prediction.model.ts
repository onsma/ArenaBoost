export interface ProjectPrediction {
  projectId: number;
  predictedSuccess: boolean;
  confidenceScore: number;
  estimatedCompletionDate: Date;
  riskLevel: string;
  recommendations: string[];
}
