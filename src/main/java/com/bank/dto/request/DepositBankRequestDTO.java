package com.bank.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositBankRequestDTO {
    private String id;
    private Double balance;
    private Double interestRate;
    private Date createdDate;
    private Double minBalance;
    private Double firstRecharge;
    private Date firstDepositDate;
    private String customerId;
    private String employeeId;
}
