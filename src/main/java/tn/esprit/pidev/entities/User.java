package tn.esprit.pidev.entities;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_user;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role userRole;

    public Long getIdUser() {
        return id_user;
    }

    // ✅ Ajout du setter pour éviter l'erreur "cannot find symbol setIdUser"
    public void setIdUser(Long idUser) {
        this.id_user = idUser;
    }
}
