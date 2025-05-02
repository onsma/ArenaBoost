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
    private long id_manager;
    private String club_name;
    private String sport_type;
    private String club_logo;
    private String description;
    private   String financial_status;
    private String email_club;
    @OneToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;  // Relation avec User
}
