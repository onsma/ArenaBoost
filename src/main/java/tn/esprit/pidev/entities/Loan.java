package tn.esprit.pidev.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_loan;
    private float amount;

    @Enumerated(EnumType.STRING)
    private Loantype loantype;

    private float interest_rate;
    private int refund_duration;

    @Enumerated(EnumType.STRING)
    private Status status;


    @Column(nullable = false, updatable = false)
    private LocalDate requestDate;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    // ✅ Cette méthode fonctionne maintenant sans erreur
    public void setUserById(Long idUser) {
        this.user = new User();
        this.user.setIdUser(idUser);
    }
}
