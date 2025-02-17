package tn.esprit.pidev.entities;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
public class Investor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_investor;
    private String phone_number;
    private float investement_balance;
    private String profil_picture;
    private String CV;
    private String risk_tolerance;
    @OneToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;
}
