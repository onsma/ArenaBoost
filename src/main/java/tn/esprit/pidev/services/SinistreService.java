package tn.esprit.pidev.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pidev.entities.Sinistre;
import tn.esprit.pidev.exception.ResourceNotFoundException;
import tn.esprit.pidev.repositories.InsuranceRepository;
import tn.esprit.pidev.repositories.SinistreRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class SinistreService {

    @Autowired
    private SinistreRepository sinistreRepository;
    @Autowired
    private InsuranceRepository insuranceRepository;
    public Sinistre createSinistreWithPicture(Sinistre sinistre, MultipartFile pictureFile) {
        // Define the upload directory
        String uploadDir = "C:\\wamp64\\www\\Uploads";
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        // Create a unique file name using the current timestamp and original file name
        String originalFilename = pictureFile.getOriginalFilename();
        String newFilename = System.currentTimeMillis() + "_" + originalFilename;
        File destination = new File(uploadFolder, newFilename);

        try {
            // Save the file to disk
            pictureFile.transferTo(destination);
        } catch (IOException e) {
            throw new RuntimeException("Error saving file", e);
        }

        // Set the absolute file path to the picture attribute
        sinistre.setPicture(destination.getAbsolutePath());

        // Save the Sinistre entity in the database
        return sinistreRepository.save(sinistre);
    }
    // Create
    public Sinistre createSinistre(Sinistre sinistre) {
        // Validate that the insurance exists using the id_insurance provided in sinistre
        insuranceRepository.findById(sinistre.getId_insurance()).orElseThrow(() -> new ResourceNotFoundException("Insurance not found with id: " + sinistre.getId_insurance()));

        // Additional logic (validate insurance, check coverage, etc.)
        return sinistreRepository.save(sinistre);
    }

    // Read all
    public List<Sinistre> getAllSinistres() {
        return sinistreRepository.findAll();
    }

    // Read by ID
    public Sinistre getSinistreById(long id) {
        return sinistreRepository.findById(id).get();
    }

    // Update
    public Sinistre updateSinistre(long id, Sinistre updatedSinistre) {
        Sinistre existing = getSinistreById(id);
        existing.setDate(updatedSinistre.getDate());
        existing.setAmount(updatedSinistre.getAmount());
        existing.setPicture(updatedSinistre.getPicture());
        // ... other logic (approval, request docs, etc.)
        return sinistreRepository.save(existing);
    }

    // Delete
    public void deleteSinistre(long id) {
        Sinistre existing = getSinistreById(id);
        // Additional logic (check if not processed, etc.)
        sinistreRepository.delete(existing);
    }
}
