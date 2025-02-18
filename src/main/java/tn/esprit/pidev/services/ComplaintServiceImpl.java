package tn.esprit.pidev.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidev.entities.Complaint;
import tn.esprit.pidev.repositories.ComplaintRepository;

import java.util.List;
@Service
public class ComplaintServiceImpl implements IComplaintService{
    @Autowired
    ComplaintRepository complaintrep;
    @Override
    public List<Complaint> getAllComplaint() {
        return complaintrep.findAll();
    }

    @Override
    public Complaint addComplaint(Complaint complaint) {
        return complaintrep.save(complaint);
    }

    @Override
    public Complaint updateComplaint(Complaint complaint) {
        return complaintrep.save(complaint);
    }

    @Override
    public void deleteComplaint(long id_complaint) {
        complaintrep.deleteById(id_complaint);

    }

    @Override
    public Complaint getComplaintById(long id_complaint) {
        return complaintrep.findById(id_complaint).get();
    }
}
