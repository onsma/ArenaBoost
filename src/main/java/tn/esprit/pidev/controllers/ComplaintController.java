package tn.esprit.pidev.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pidev.entities.Complaint;
import tn.esprit.pidev.entities.User;
import tn.esprit.pidev.services.IComplaintService;
import tn.esprit.pidev.services.IUserService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/complaint")
public class ComplaintController {
    @Autowired
    IComplaintService complaintservice;

    @GetMapping("/getComplaint")
    public List<Complaint> getAllComplaint(){
        return complaintservice.getAllComplaint();
    }
    @PostMapping("/addComplaint")
    public Complaint addComplaint(@RequestBody Complaint complaint){
        return complaintservice.addComplaint(complaint);
    }
    @DeleteMapping("/deleteComplaint/{id_complaint}")
    public void deleteComplaint(@PathVariable long id_complaint){
        complaintservice.deleteComplaint(id_complaint);
    }
}
