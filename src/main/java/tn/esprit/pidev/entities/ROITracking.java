package tn.esprit.pidev.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
public class ROITracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roi_id")
    Long roiId;

    @ManyToOne
    @JoinColumn(name = "investment_id", nullable = false)
    Investment investment;

    @ManyToOne
    @JoinColumn(name = "athlete_id")
    Athlete athlete;

    @ManyToOne
    @JoinColumn(name = "project_id")
    Project project;

    @Column(name = "roi_amount", nullable = false)
    Double roiAmount;

    @Column(name = "roi_date", nullable = false)
    Date roiDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    Date createdAt;

    @Column(name = "updated_at")
    Date updatedAt;
}
