package tn.esprit.pidev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.pidev.entities.Investor;

@Repository
public interface InvestorRepository extends JpaRepository<Investor, Long> {
}
