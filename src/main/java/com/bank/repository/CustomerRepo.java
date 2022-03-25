package com.bank.repository;

import com.bank.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepo extends JpaRepository<Customer, String> {
    List<Customer> findAll();
    Optional<Customer> findCustomerByAccountId(String accountId);
    Optional<Customer> findCustomerById(String id);
}
