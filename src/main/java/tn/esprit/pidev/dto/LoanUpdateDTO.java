package tn.esprit.pidev.dto;

import lombok.Data;

@Data
public class LoanUpdateDTO {
    private float amount;
    private String loantype;
    private float interest_rate;
    private int refund_duration;
    private String status;
}
