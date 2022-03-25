package com.bank.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditBankRequestDTO {
    private String id;
    private Double balance;
    private Double interestRate;
    private Date createdDate;
    private Double maxLoan;
    private Date expirationDate;
    private String customerId;
    private String employeeId;
}
