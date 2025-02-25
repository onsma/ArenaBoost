package tn.esprit.pidev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.pidev.entities.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
