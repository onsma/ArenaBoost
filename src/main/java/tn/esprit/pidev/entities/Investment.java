package tn.esprit.pidev.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Setter
@Getter
@Entity

public class Investment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "investment_id")
    Long investmentId;

    @ManyToOne
    @JoinColumn(name = "investor_id", referencedColumnName = "investor_id", nullable = false)
    private Investor investor;

    @OneToOne(mappedBy = "investment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EmailDetails emailDetails;

    @ManyToOne
    @JoinColumn(name = "id_athelete")
    Athlete athlete;

    @ManyToOne
    @JoinColumn(name = "id_project")
    Project project;

    @Column(name = "amount", nullable = false)
    Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "investment_type", nullable = false)
    InvestmentType investmentType;

    @Column(name = "expected_roi")
    private Double expectedROI; // Expected ROI at the time of investment

    @Column(name = "actual_roi")
    private Double actualROI; // Actual ROI after investment completion

    @Column(name = "start_date", nullable = false)
    Date startDate;

    @Column(name = "end_date")
    Date endDate;

    @Column(name = "roi_percentage")
    Double roiPercentage;

    @Column(name = "description")
    private String description; // Description or notes about the investment

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Getters and Setters
    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    @Column(name = "exit_amount")
    private Double exitAmount; // Amount received upon exiting the investment

    @Column(name = "dividend_rate")
    private Double dividendRate; // Dividend rate (if applicab

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    InvestmentStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    Date createdAt;

    @Column(name = "updated_at")
    Date updatedAt;

    @Column(name = "dividend_payment_frequency")
    @Enumerated(EnumType.STRING)
    private DividendPaymentFrequency dividendPaymentFrequency;

    @OneToMany(mappedBy = "investment")  // Un investissement peut avoir plusieurs transactions
    private List<Transaction> transactions;

    public Long getInvestmentId() {
        return investmentId;
    }

    public void setInvestmentId(Long investmentId) {
        this.investmentId = investmentId;
    }

    public Investor getInvestor() {
        return investor;
    }

    public void setInvestor(Investor investor) {
        this.investor = investor;
    }

    public Athlete getAthlete() {
        return athlete;
    }

    public void setAthlete(Athlete athlete) {
        this.athlete = athlete;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public InvestmentType getInvestmentType() {
        return investmentType;
    }

    public void setInvestmentType(InvestmentType investmentType) {
        this.investmentType = investmentType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Double getRoiPercentage() {
        return roiPercentage;
    }

    public void setRoiPercentage(Double roiPercentage) {
        this.roiPercentage = roiPercentage;
    }

    public InvestmentStatus getStatus() {
        return status;
    }

    public void setStatus(InvestmentStatus status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

    public Investment() {
        this.isActive = true; // Default value
    }
}
