package tn.esprit.pidev.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.ChargeCollection;
import com.stripe.model.checkout.Session;
import com.stripe.param.ChargeListParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pidev.dto.ChargeDto;
import tn.esprit.pidev.entities.Sinistre;
import tn.esprit.pidev.services.PaymentService;
import tn.esprit.pidev.services.SinistreService;

import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/sinistres")
public class SinistreController {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Autowired
    private SinistreService sinistreService;
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, Object>> createCheckoutSession(@RequestBody Sinistre sinistre) {
        try {

            // 1) Initialize Stripe
            Stripe.apiKey = stripeApiKey;

            // 2) Convert your Sinistre amount to the smallest currency unit (e.g., cents)
            long amountInCents = Math.round(sinistre.getAmount() * 100);

            // 3) Build the line items
            List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
            lineItems.add(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("usd")
                                            .setUnitAmount(amountInCents)
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName("Sinistre ID: " + sinistre.getId_sinistre())
                                                            .build()
                                            )
                                            .build()
                            )
                            .build()
            );

            // 4) Build session params
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("https://example.com/success")
                    .setCancelUrl("https://example.com/cancel")
                    .addAllLineItem(lineItems)
                    .build();

            // 5) Create session
            Session session = Session.create(params);

            // 6) Return the session URL to your client
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("checkoutUrl", session.getUrl());

            return ResponseEntity.ok(responseData);

        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // Create
    @PostMapping
    public ResponseEntity<Sinistre> createSinistre(@RequestBody Sinistre sinistre) {
//        try {
//            PaymentIntent intent = paymentService.createPaymentIntent(sinistre);
//        } catch (StripeException e) {
//            e.printStackTrace();
//            return ResponseEntity.badRequest().build();
//        }
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
    //CREATE SISNISTER WITH IMAGE PART
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Sinistre> createSinistreWithPicture(
            @RequestPart("sinistre") String sinistreJson,
            @RequestPart("picture") MultipartFile pictureFile) throws JsonProcessingException {

        // Manually parse the JSON string into a Sinistre object
        Sinistre sinistre = new ObjectMapper().readValue(sinistreJson, Sinistre.class);

        Sinistre created = sinistreService.createSinistreWithPicture(sinistre, pictureFile);
        return ResponseEntity.ok(created);
    }

    //GET ALL TRANSACTION
    @GetMapping("/transactions")
    public ResponseEntity<List<ChargeDto>> getTransactions() {
        try {
            // 1) Initialize Stripe
            Stripe.apiKey = stripeApiKey; // ensure stripeApiKey is defined

            // 2) Build params (limit to 100 charges, etc.)
            ChargeListParams params = ChargeListParams.builder()
                    .setLimit(2L)
                    .build();

            // 3) Retrieve the charges
            ChargeCollection charges = Charge.list(params);

            // 4) Build a simplified list of ChargeDto
            List<ChargeDto> result = new ArrayList<>();
            for (Charge c : charges.getData()) {
                // Extract email, name, and country from billing_details if available
                String email = null;
                String name = null;
                String country = null;
                if (c.getBillingDetails() != null) {
                    email = c.getBillingDetails().getEmail();
                    name = c.getBillingDetails().getName();
                    if (c.getBillingDetails().getAddress() != null) {
                        country = c.getBillingDetails().getAddress().getCountry();
                    }
                }
                String receiptUrl = c.getReceiptUrl();

                ChargeDto dto = new ChargeDto(
                        c.getId(),
                        c.getAmount(),
                        c.getCurrency(),
                        c.getStatus(),
                        c.getDescription(),
                        c.getCreated(),
                        email,
                        receiptUrl,
                        name,
                        country
                );
                result.add(dto);
            }
            return ResponseEntity.ok(result);
        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    //CREATE SISNISTER WITH IMAGE PART  ADN STRIPE PAYEMENT PART

}
