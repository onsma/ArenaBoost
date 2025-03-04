package tn.esprit.pidev.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.pidev.entities.Investment;

import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long>, JpaSpecificationExecutor<Investment> {

    List<Investment> findByInvestor_Id(Long investorId);
    List<Investment> findAll(Specification<Investment> spec);
//    List<Investment> findByAthleteId(Long id_athelete);
//    List<Investment> findByProjectId(Long id_project);
//@Query("SELECT i FROM Investment i WHERE " +
//        "(:riskLevel IS NULL OR i. = :riskLevel) AND " +
//        "(:minExpectedROI IS NULL OR i.expectedROI >= :minExpectedROI) AND " +
//        "(:maxExpectedROI IS NULL OR i.expectedROI <= :maxExpectedROI) AND " +
//        "(:minAmount IS NULL OR i.amount >= :minAmount) AND " +
//        "(:maxAmount IS NULL OR i.amount <= :maxAmount)")
//List<Investment> findByFilters(
//        @Param("riskLevel") String riskLevel,
//        @Param("minExpectedROI") Double minExpectedROI,
//        @Param("maxExpectedROI") Double maxExpectedROI,
//        @Param("minAmount") Double minAmount,
//        @Param("maxAmount") Double maxAmount
//);
}
