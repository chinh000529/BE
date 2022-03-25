package com.bank.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;

@Entity(name = "DepositAccount")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositAccount {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "min_balance", nullable = false)
    private Double minBalance;

    @Column(name = "first_recharge", nullable = true)
    private Double firstRecharge;

    @Column(name = "first_deposit_date", nullable = true)
    private Date firstDepositDate;
}
