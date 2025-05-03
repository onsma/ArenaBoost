package tn.esprit.pidev.services;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tn.esprit.pidev.dto.LoanSearchCriteria;
import tn.esprit.pidev.dto.LoanStatisticsDTO;
import tn.esprit.pidev.entities.Loan;
import tn.esprit.pidev.entities.Status;
import tn.esprit.pidev.entities.User;
import tn.esprit.pidev.repositories.LoanRepository;
import tn.esprit.pidev.repositories.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final MailService mailService;  // Ajout du service d'e-mail

    @Autowired
    public LoanService(LoanRepository loanRepository, UserRepository userRepository, MailService mailService) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    @Transactional
    public Loan createLoan(Loan loan, Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("L'utilisateur lié au prêt n'existe pas !");
        }

        loan.setUser(userOptional.get());
        loan.setRequestDate(LocalDate.now());
        // S'assurer que le statut est bien défini à PENDING
        if (loan.getStatus() == null) {
            loan.setStatus(Status.pending);
        }
        Loan savedLoan = loanRepository.save(loan);

        // 📩 Envoi d'un e-mail pour informer que la demande est en cours d'analyse
        mailService.envoyerNotificationCreationLoan(savedLoan);

        return savedLoan;

    }

    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @Transactional
    public Loan updateLoan(Long id, Loan loanDetails) {
        return loanRepository.findById(id).map(existingLoan -> {
            existingLoan.setAmount(loanDetails.getAmount());
            existingLoan.setLoantype(loanDetails.getLoantype());
            existingLoan.setInterest_rate(loanDetails.getInterest_rate());
            existingLoan.setRefund_duration(loanDetails.getRefund_duration());
            existingLoan.setStatus(loanDetails.getStatus());

            Loan updatedLoan = loanRepository.save(existingLoan);

            // 📩 Envoi automatique d'un e-mail au demandeur du prêt
            mailService.envoyerNotificationLoan(updatedLoan);

            return updatedLoan;
        }).orElseThrow(() -> new RuntimeException("Prêt non trouvé avec l'ID : " + id));
    }

    @Transactional
    public boolean deleteLoan(Long id) {
        if (loanRepository.existsById(id)) {
            loanRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public LoanStatisticsDTO getLoanStatistics() {
        List<Loan> allLoans = loanRepository.findAll();
        System.out.println("Found " + allLoans.size() + " loans in the database");

        long totalLoans = allLoans.size();
        double totalAmount = allLoans.stream().mapToDouble(Loan::getAmount).sum();
        double averageDuration = allLoans.stream().mapToDouble(Loan::getRefund_duration).average().orElse(0);

        long pendingLoans = allLoans.stream().filter(loan -> loan.getStatus().name().equalsIgnoreCase("PENDING")).count();
        long approvedLoans = allLoans.stream().filter(loan -> loan.getStatus().name().equalsIgnoreCase("APPROVED")).count();
        long rejectedLoans = allLoans.stream().filter(loan -> loan.getStatus().name().equalsIgnoreCase("REJECTED")).count();

        System.out.println("Loan statistics: " + totalLoans + " total, " + pendingLoans + " pending, " +
                          approvedLoans + " approved, " + rejectedLoans + " rejected, " +
                          totalAmount + " total amount, " + averageDuration + " avg duration");

        return new LoanStatisticsDTO(totalLoans, totalAmount, averageDuration, pendingLoans, approvedLoans, rejectedLoans);
    }
    // 🎯 Recherche avancée des prêts avec Criteria API
    // Récupérer les prêts récents (derniers 30 jours)
    public List<Loan> getRecentLoans() {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        List<Loan> recentLoans = loanRepository.findAll().stream()
                .filter(loan -> loan.getRequestDate().isAfter(thirtyDaysAgo))
                .collect(Collectors.toList());
        System.out.println("Found " + recentLoans.size() + " recent loans");
        return recentLoans;
    }

    // Récupérer les prêts en cours (status = PENDING)
    public List<Loan> getCurrentLoans() {
        List<Loan> currentLoans = loanRepository.findAll().stream()
                .filter(loan -> loan.getStatus() == Status.pending)
                .collect(Collectors.toList());
        System.out.println("Found " + currentLoans.size() + " current loans with status PENDING");
        return currentLoans;
    }

    // Récupérer les prêts récemment approuvés
    public List<Loan> getRecentlyApprovedLoans() {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        List<Loan> approvedLoans = loanRepository.findAll().stream()
                .filter(loan -> loan.getStatus() == Status.approved)
                .filter(loan -> loan.getRequestDate().isAfter(thirtyDaysAgo))
                .collect(Collectors.toList());
        System.out.println("Found " + approvedLoans.size() + " recently approved loans");
        return approvedLoans;
    }

    public List<Loan> searchLoans(LoanSearchCriteria criteria) {
        Specification<Loan> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (criteria.getMinAmount() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("amount"), criteria.getMinAmount()));
            }
            if (criteria.getMaxAmount() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("amount"), criteria.getMaxAmount()));
            }
            if (criteria.getLoantype() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("loantype"), criteria.getLoantype()));
            }
            if (criteria.getMinInterestRate() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("interest_rate"), criteria.getMinInterestRate()));
            }
            if (criteria.getMaxInterestRate() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("interest_rate"), criteria.getMaxInterestRate()));
            }
            if (criteria.getMinDuration() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("refund_duration"), criteria.getMinDuration()));
            }
            if (criteria.getMaxDuration() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("refund_duration"), criteria.getMaxDuration()));
            }
            if (criteria.getStatus() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), criteria.getStatus()));
            }
            if (criteria.getUserKeyword() != null) {
                Join<Object, Object> userJoin = root.join("user", JoinType.INNER);
                Predicate namePredicate = cb.like(userJoin.get("firstName"), "%" + criteria.getUserKeyword() + "%");
                Predicate emailPredicate = cb.like(userJoin.get("email"), "%" + criteria.getUserKeyword() + "%");
                predicate = cb.and(predicate, cb.or(namePredicate, emailPredicate));
            }

            return predicate;
        };

        return loanRepository.findAll(spec);
    }
}