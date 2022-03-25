package com.bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {
    private String id;
    private String customerId;
    private String customerName;
    private Double amount;
    private String type;
    private String idBankSource;
    private String idBankTarget;
    private Date transactionDate;
    private String content;
}
