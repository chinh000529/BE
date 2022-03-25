package com.bank.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;

@Entity(name = "Customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "id_card", nullable = false)
    private String idCard;

    @Column(name = "fullname", nullable = false)
    private String fullName;

    @Column(name = "phonenumber", nullable = false)
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "address")
    private String address;

    @Column(name = "account_id", nullable = false)
    private String accountId;
}
