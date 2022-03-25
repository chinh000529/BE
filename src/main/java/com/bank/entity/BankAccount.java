package com.bank.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;

@Entity(name = "BankAccount")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "balance")
    private Double balance;

    @Column(name = "interest_rate", nullable = false)
    private Double interestRate;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;
}
