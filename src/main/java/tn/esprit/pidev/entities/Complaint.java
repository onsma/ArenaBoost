package tn.esprit.pidev.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_reclamation;
    private long id_user;
    private ComplaintType complaintType;
    private String description;
    private Date submissionDate;
    private Date resolutionDate;
    private Priority priority;
    private String attechedFiles;

}
