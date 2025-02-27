package tn.esprit.pidev.dto;

public class LoanStatisticsDTO {
    private long totalLoans;
    private double totalAmount;
    private double averageDuration;
    private long pendingLoans;
    private long approvedLoans;
    private long rejectedLoans;

    public LoanStatisticsDTO(long totalLoans, double totalAmount, double averageDuration, long pendingLoans, long approvedLoans, long rejectedLoans) {
        this.totalLoans = totalLoans;
        this.totalAmount = totalAmount;
        this.averageDuration = averageDuration;
        this.pendingLoans = pendingLoans;
        this.approvedLoans = approvedLoans;
        this.rejectedLoans = rejectedLoans;
    }

    // ✅ Getters et Setters
    public long getTotalLoans() { return totalLoans; }
    public void setTotalLoans(long totalLoans) { this.totalLoans = totalLoans; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getAverageDuration() { return averageDuration; }
    public void setAverageDuration(double averageDuration) { this.averageDuration = averageDuration; }

    public long getPendingLoans() { return pendingLoans; }
    public void setPendingLoans(long pendingLoans) { this.pendingLoans = pendingLoans; }

    public long getApprovedLoans() { return approvedLoans; }
    public void setApprovedLoans(long approvedLoans) { this.approvedLoans = approvedLoans; }

    public long getRejectedLoans() { return rejectedLoans; }
    public void setRejectedLoans(long rejectedLoans) { this.rejectedLoans = rejectedLoans; }
}
