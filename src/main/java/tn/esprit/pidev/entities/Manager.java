package tn.esprit.pidev.entities;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
public class Manager {




    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_manager")
    private long idManager;

    @Column(name = "manager_name")  // Updated field name
    private String managerName;

    @Column(name = "sport")
    private String sport;

    @Column(name = "club_logo")
    private String clubLogo;

    @Column(name = "description")
    private String description;

    @Column(name = "financial_status")  // Updated field name
    private String financialStatus;

    @Column(name = "email_club")
    private String emailClub;


    @OneToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;  // Relation avec User
}
