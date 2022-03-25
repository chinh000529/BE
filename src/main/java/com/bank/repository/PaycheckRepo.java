package com.bank.repository;

import com.bank.entity.Paycheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaycheckRepo extends JpaRepository<Paycheck,String> {
    List<Paycheck> findAllByMonthAndYear(Integer month, Integer year);
    List<Paycheck> findAllByEmployeeId(String employeeId);
    List<Paycheck> findAllByMonthAndYearAndEmployeeId(Integer month, Integer year,String employeeId);
}
