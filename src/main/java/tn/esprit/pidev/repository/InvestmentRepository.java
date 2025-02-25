package tn.esprit.pidev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.pidev.entities.Investment;

import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    List<Investment> findByInvestor_Id(Long investorId);

//    List<Investment> findByAthleteId(Long id_athelete);
//    List<Investment> findByProjectId(Long id_project);

}
