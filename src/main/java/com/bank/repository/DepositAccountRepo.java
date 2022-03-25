package com.bank.repository;

import com.bank.entity.DepositAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepositAccountRepo extends JpaRepository<DepositAccount,String> {
    Optional<DepositAccount> findDepositAccountById(String id);
}
