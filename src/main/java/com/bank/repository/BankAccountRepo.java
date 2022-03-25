package com.bank.repository;

import com.bank.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepo extends JpaRepository<BankAccount,String> {
    Optional<BankAccount> findBankAccountById(String id);
    Optional<BankAccount> findBankAccountByIdAndType(String id, String type);
    Optional<BankAccount> findBankAccountByCustomerIdAndType(String customerId,String type);
    List<BankAccount> findAllByType(String type);
}
