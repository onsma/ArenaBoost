package tn.esprit.pidev.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pidev.entities.User;
import tn.esprit.pidev.services.IUserService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {
    @Autowired
    IUserService userservice;

    @GetMapping("/getUsers")
    public List<User> getAllUsers(){
        return userservice.getAllUsers();
    }
    @PostMapping("/addUser")
    public User addUser(@RequestBody User user){
        return userservice.addUser(user);
    }
    @DeleteMapping("/deleteUser/{id_user}")
    public void deleteUser(@PathVariable long id_user){
        userservice.getUserById(id_user);
    }
}
