package tn.esprit.pidev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.pidev.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
