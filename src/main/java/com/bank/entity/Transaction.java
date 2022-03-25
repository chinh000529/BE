package com.bank.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;

@Entity(name = "Transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "id_bank_source")
    private String idBankSource;

    @Column(name = "id_bank_target", nullable = false)
    private String idBankTarget;

    @Column(name = "transaction_amount", nullable = false)
    private Double transactionAmount;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "transaction_date", nullable = false)
    private Date transactionDate;

    @Column(name = "content")
    private String content;
}
