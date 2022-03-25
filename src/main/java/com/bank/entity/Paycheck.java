package com.bank.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;

@Entity(name = "Paycheck")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paycheck {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "monthly_salary", nullable = false)
    private Double monthlySalary;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "year", nullable = false)
    private Integer year;
}
