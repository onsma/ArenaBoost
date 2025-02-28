package tn.esprit.pidev.entities;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
public class Supporter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_supporter;
    private String phone_number;
    private String favourite_club;
    @OneToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;
}
