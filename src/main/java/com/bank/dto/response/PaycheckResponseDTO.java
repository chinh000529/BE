package com.bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaycheckResponseDTO {
    private String paycheckId;
    private String employeeId;
    private String employeeName;
    private Double amount;
    private Double monthlySalary;
    private Date createdDate;
    private Integer month;
    private Integer year;
}
