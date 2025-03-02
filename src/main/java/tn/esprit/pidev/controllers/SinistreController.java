package tn.esprit.pidev.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pidev.entities.Sinistre;
import tn.esprit.pidev.services.SinistreService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/sinistres")
public class SinistreController {

    @Autowired
    private SinistreService sinistreService;

    // Create
    @PostMapping
    public ResponseEntity<Sinistre> createSinistre(@RequestBody Sinistre sinistre) {
        Sinistre created = sinistreService.createSinistre(sinistre);
        return ResponseEntity.ok(created);
    }

    // Read all
    @GetMapping
    public List<Sinistre> getAllSinistres() {
        return sinistreService.getAllSinistres();
    }

    // Read by ID
    @GetMapping("/{id}")
    public ResponseEntity<Sinistre> getSinistreById(@PathVariable long id) {
        Sinistre sinistre = sinistreService.getSinistreById(id);
        return ResponseEntity.ok(sinistre);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<Sinistre> updateSinistre(@PathVariable long id, @RequestBody Sinistre updatedSinistre) {
        Sinistre result = sinistreService.updateSinistre(id, updatedSinistre);
        return ResponseEntity.ok(result);
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSinistre(@PathVariable long id) {
        sinistreService.deleteSinistre(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Sinistre> createSinistreWithPicture(
            @RequestPart("sinistre") String sinistreJson,
            @RequestPart("picture") MultipartFile pictureFile) throws JsonProcessingException {

        // Manually parse the JSON string into a Sinistre object
        Sinistre sinistre = new ObjectMapper().readValue(sinistreJson, Sinistre.class);

        Sinistre created = sinistreService.createSinistreWithPicture(sinistre, pictureFile);
        return ResponseEntity.ok(created);
    }

}
