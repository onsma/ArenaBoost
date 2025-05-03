export interface LoanType {
  id_loantype: number;
  name: string;
  description: string;
}

export interface Loan {
  id_loan?: number;
  amount: number;
  loantype: LoanType;
  interest_rate: number;
  refund_duration: number;
  status?: string;
  requestDate?: string;
  user: {
    id_user: number;
  };
}
