package tn.esprit.pidev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import tn.esprit.pidev.entities.Loan;
import tn.esprit.pidev.entities.User;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {
    List<Loan> findByUser(User user);
}
