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
public class Insurance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_insurance;
    private TypeInsurance typeInsurance;
    //private long id_user;
    private float amount;
    private Date subscription_date;
    private Date renewal_date;
    private StatusInsurance statusInsurance;
    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;  // L'utilisateur qui a souscrit à l'assurance
}
