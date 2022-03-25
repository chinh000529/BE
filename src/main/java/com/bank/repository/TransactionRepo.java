package com.bank.repository;

import com.bank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface TransactionRepo extends JpaRepository<Transaction,String> {
    List<Transaction> findAllByTransactionDateLessThanEqualAndTransactionDateGreaterThanEqual(Date endDate, Date startDate);
    List<Transaction> findAllByIdBankSourceAndTransactionDateLessThanEqualAndTransactionDateGreaterThanEqual(String idBankSource, Date endDate, Date startDate);
    List<Transaction> findAllByIdBankTargetAndTransactionDateLessThanEqualAndTransactionDateGreaterThanEqual(String idBankTarget, Date endDate, Date startDate);
    List<Transaction> findAllByIdBankSourceOrIdBankTarget(String idBank1, String idBank2);
    Optional<Transaction> findById(String id);
}
