package com.bank.repository;

import com.bank.entity.CreditAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreditAccountRepo extends JpaRepository<CreditAccount,String> {
    Optional<CreditAccount> findCreditAccountById(String id);
}
