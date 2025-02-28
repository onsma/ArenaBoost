package tn.esprit.pidev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.pidev.entities.Insurance;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, Long> {
    // Additional query methods if needed
}
