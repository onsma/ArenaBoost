package tn.esprit.pidev.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidev.entities.User;
import tn.esprit.pidev.repositories.UserRepository;

import java.util.List;
@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService{
    @Autowired
    UserRepository userrep;
    @Override
    public List<User> getAllUsers() {
        return userrep.findAll();
    }

    @Override
    public User addUser(User user) {
        return userrep.save(user);
    }

    @Override
    public User updateUser(User user) {
        return userrep.save(user);
    }

    @Override
    public void deleteUser(long id_user) {
    userrep.deleteById(id_user);
    }

    @Override
    public User getUserById(long id_user) {
        return userrep.findById(id_user).get();
    }
}
