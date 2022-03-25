package com.bank.repository;

import com.bank.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepo extends JpaRepository<Employee,String> {
    Optional<Employee> findEmployeeById(String id);
    Optional<Employee> findEmployeeByAccountId(String accountId);
}
