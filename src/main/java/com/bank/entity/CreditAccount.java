package com.bank.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;

@Entity(name = "CreditAccount")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditAccount {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "max_loan", nullable = false)
    private Double maxLoan;

    @Column(name = "expiration_date", nullable = false)
    private Date expirationDate;
}
