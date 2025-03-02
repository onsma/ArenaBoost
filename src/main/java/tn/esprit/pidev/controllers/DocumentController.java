package tn.esprit.pidev.controllers;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import tn.esprit.pidev.services.DocumentService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping("/loan-contract/{loanId}")
    public ResponseEntity<byte[]> getLoanContract(@PathVariable Long loanId) {
        try {
            File file = documentService.generateLoanContract(loanId);
            byte[] contents = Files.readAllBytes(file.toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", file.getName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(contents);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
