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
public class Athlete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_athelete;
    private Date birthDate;
    private String sexe;
    private String sport;
    private String profil_picture;
    private String phone_number;
    private String CV;
    @OneToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;
    @OneToMany(mappedBy = "athlete")
    private List<Loan> loans; // Liste des prêts liés à cet utilisateur
}
