package tn.esprit.pidev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.pidev.entities.Manager;

import java.util.List;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
//    List<Manager> findByManagerNameContainingAndSportAndFinancialStatus(
//            String managerName, String sport, String financialStatus);
//    List<Manager> findByManagerNameContaining(String managerName);
//    List<Manager> findByManagerNameContainingAndFinancialStatus(String managerName,String financialStatus);
//    List<Manager> findByManagerNameContainingAndSport(String managerName , String sport);

    // Search managers by name (derived query method)
    List<Manager> findManagerByManagerNameContaining(String name);

    // Search managers by sport (derived query method)
    List<Manager> findManagerBySport(String sport);

    // Search managers by financial status (derived query method)
    List<Manager> findManagerByFinancialStatus(String financialStatus );
}
