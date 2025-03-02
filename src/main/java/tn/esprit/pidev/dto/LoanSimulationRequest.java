package tn.esprit.pidev.dto;

public class LoanSimulationRequest {
    private double amount;
    private int duration; // en mois
    private double interestRate; // en pourcentage

    // Constructeurs, Getters et Setters
    public LoanSimulationRequest() {}

    public LoanSimulationRequest(double amount, int duration, double interestRate) {
        this.amount = amount;
        this.duration = duration;
        this.interestRate = interestRate;
    }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
}
