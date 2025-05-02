package tn.esprit.pidev.services;

import tn.esprit.pidev.entities.Complaint;
import tn.esprit.pidev.entities.User;

import java.util.List;

public interface IComplaintService {
    public List<Complaint> getAllComplaint();
    public Complaint addComplaint(Complaint complaint);
    public Complaint updateComplaint  (Complaint complaint);
    public void deleteComplaint(long id_complaint);
    public Complaint getComplaintById(long id_complaint);
}
