package tn.esprit.pidev.services;

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

    @Autowired
    public LoanService(LoanRepository loanRepository, UserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
    }

    public Loan createLoan(Loan loan, Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) { // Utilisation correcte de Optional
            throw new RuntimeException("L'utilisateur lié au prêt n'existe pas !");
        }

        loan.setUser(userOptional.get()); // Associer l'utilisateur au prêt
        return loanRepository.save(loan); // Enregistrer le prêt
    }


    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Loan updateLoan(Long id, Loan loanDetails) {
        return loanRepository.findById(id).map(existingLoan -> {
            existingLoan.setAmount(loanDetails.getAmount());
            existingLoan.setLoantype(loanDetails.getLoantype());
            existingLoan.setInterest_rate(loanDetails.getInterest_rate());
            existingLoan.setRefund_duration(loanDetails.getRefund_duration());
            existingLoan.setStatus(loanDetails.getStatus());
            return loanRepository.save(existingLoan);
        }).orElse(null);
    }

    public boolean deleteLoan(Long id) {
        if (loanRepository.existsById(id)) {
            loanRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
