package tn.esprit.pidev.services;

import tn.esprit.pidev.entities.User;

import java.util.List;

public interface IUserService {
    public List<User> getAllUsers();
    public User addUser(User user);
    public User updateUser  (User user);
    public void deleteUser(long id_user);
    public User getUserById(long id_user);
}
