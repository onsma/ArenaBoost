package tn.esprit.pidev.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidev.entities.User;
import tn.esprit.pidev.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public long countUsers() {
        return userRepository.count();
    }

    // Pour l'instant, on considère tous les utilisateurs comme actifs
    // Dans une implémentation réelle, on pourrait ajouter un champ "active" dans l'entité User
    public long countActiveUsers() {
        return userRepository.count();
    }

    // Récupérer les utilisateurs actifs
    // Dans une implémentation réelle, on filtrerait sur un champ "active"
    public List<User> getActiveUsers() {
        return userRepository.findAll();
    }
}
