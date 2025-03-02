package tn.esprit.pidev.dto;

public class LoanSimulationResponse {
    private double monthlyPayment;
    private double totalInterest;
    private double totalAmount;

    public LoanSimulationResponse(double monthlyPayment, double totalInterest, double totalAmount) {
        this.monthlyPayment = monthlyPayment;
        this.totalInterest = totalInterest;
        this.totalAmount = totalAmount;
    }

    // Getters et Setters
    public double getMonthlyPayment() { return monthlyPayment; }
    public void setMonthlyPayment(double monthlyPayment) { this.monthlyPayment = monthlyPayment; }

    public double getTotalInterest() { return totalInterest; }
    public void setTotalInterest(double totalInterest) { this.totalInterest = totalInterest; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}
