package com.bank.repository;

import com.bank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepo extends JpaRepository<Account, String> {
    Optional<Account> findAccountByUsernameAndPassword(String username,String password);
    Optional<Account> findAccountByUsername(String username);
    Optional<Account> findAccountByUsernameAndRole(String username, String role);
    Optional<Account> findAccountById(String id);
}
