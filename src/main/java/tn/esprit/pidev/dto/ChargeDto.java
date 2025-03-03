package tn.esprit.pidev.dto;

public record ChargeDto(
        String id,
        Long amount,
        String currency,
        String status,
        String description,
        Long created,
        String email,
        String receiptUrl,
        String name,
        String country
) {}
