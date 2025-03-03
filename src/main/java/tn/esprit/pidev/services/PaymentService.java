package tn.esprit.pidev.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tn.esprit.pidev.entities.Sinistre;

@Service
public class PaymentService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    /**
     * Creates a Stripe PaymentIntent based on the Sinistre amount.
     * The amount is converted to cents (if using USD).
     */
    public PaymentIntent createPaymentIntent(Sinistre sinistre) throws StripeException {
        // Set your Stripe API key
        Stripe.apiKey = stripeApiKey;

        // Convert amount to cents; assuming sinistre.getAmount() is in dollars
        long amountInCents = Math.round(sinistre.getAmount() * 100);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd") // Change to your currency code if needed
                .setDescription("Payment for Sinistre ID: " + sinistre.getId_sinistre())
                .build();

        return PaymentIntent.create(params);
    }
}
