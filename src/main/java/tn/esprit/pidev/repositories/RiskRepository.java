package tn.esprit.pidev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.pidev.entities.Risk;

import java.util.List;

@Repository
public interface RiskRepository extends JpaRepository <Risk,Long> {
    List<Risk> findAll();
    @Query("SELECT r FROM Risk r WHERE r.risk_id = :risk_id")
    Risk findByRiskId(Long risk_id);

}
