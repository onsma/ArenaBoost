import { RiskType } from './risk-type.enum';

export interface Risk {
  risk_id?: number;
  score: number;
  probability: number;
  impact: number;
  last_date: Date;
  description: string; // Assuming Description is a string; update if needed
  risktype: RiskType;
  amount: number;
  loan: { id_loan: number }; // Simplified Loan; update if more fields are needed
}