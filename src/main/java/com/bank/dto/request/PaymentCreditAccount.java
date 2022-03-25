package com.bank.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreditAccount {
    private String depositAccountId;
    private String creditAccountId;
    private Double money;
    private String content;
}
