package tn.esprit.pidev.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidev.entities.Loan;
import tn.esprit.pidev.entities.User;
import tn.esprit.pidev.repositories.LoanRepository;
import tn.esprit.pidev.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

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
        return loanRepository.save(loan);
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
}
