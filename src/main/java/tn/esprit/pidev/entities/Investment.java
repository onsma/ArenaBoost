package tn.esprit.pidev.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
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
    @JoinColumn(name = "investor_id", nullable = false)
    Investor investor;

    @ManyToOne
    @JoinColumn(name = "athlete_id")
    Athlete athlete;

    @ManyToOne
    @JoinColumn(name = "project_id")
    Project project;

    @Column(name = "amount", nullable = false)
    Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "investment_type", nullable = false)
    InvestmentType investmentType;

    @Column(name = "start_date", nullable = false)
    Date startDate;

    @Column(name = "end_date")
    Date endDate;

    @Column(name = "roi_percentage")
    Double roiPercentage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    InvestmentStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    Date createdAt;

    @Column(name = "updated_at")
    Date updatedAt;

    @OneToMany(mappedBy = "investment")  // Un investissement peut avoir plusieurs transactions
    private List<Transaction> transactions;
}
