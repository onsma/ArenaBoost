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

    @Column(name = "net_profit")
    private Double netProfit;

    @Column(name = "current_value")
    private Double currentValue;

    @Column(name = "exit_amount")
    private Double exitAmount; // Use Double instead of double

    @Column(name = "investor_notes")
    private String investorNotes;


    @Column(name = "currency")
    private String currency;

    @Column(name = "investor_satisfaction")
    private Integer investorSatisfaction;
    @OneToMany(mappedBy = "investment")
    private List<Transaction> transactions;


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

    public EmailDetails getEmailDetails() {
        return emailDetails;
    }

    public void setEmailDetails(EmailDetails emailDetails) {
        this.emailDetails = emailDetails;
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

    public Double getExpectedROI() {
        return expectedROI;
    }

    public void setExpectedROI(Double expectedROI) {
        this.expectedROI = expectedROI;
    }

    public Double getActualROI() {
        return actualROI;
    }

    public void setActualROI(Double actualROI) {
        this.actualROI = actualROI;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Double getExitAmount() {
        return exitAmount;
    }

    public void setExitAmount(Double exitAmount) {
        this.exitAmount = exitAmount;
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

    public DividendPaymentFrequency getDividendPaymentFrequency() {
        return dividendPaymentFrequency;
    }

    public void setDividendPaymentFrequency(DividendPaymentFrequency dividendPaymentFrequency) {
        this.dividendPaymentFrequency = dividendPaymentFrequency;
    }

    public Double getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(Double netProfit) {
        this.netProfit = netProfit;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Double currentValue) {
        this.currentValue = currentValue;
    }

    public String getInvestorNotes() {
        return investorNotes;
    }

    public void setInvestorNotes(String investorNotes) {
        this.investorNotes = investorNotes;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getInvestorSatisfaction() {
        return investorSatisfaction;
    }

    public void setInvestorSatisfaction(Integer investorSatisfaction) {
        this.investorSatisfaction = investorSatisfaction;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
