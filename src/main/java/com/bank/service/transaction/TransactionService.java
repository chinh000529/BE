package com.bank.service.transaction;

import com.bank.dto.request.TransactionRequest;
import com.bank.dto.response.TransactionResponseDTO;

import java.util.List;

public interface TransactionService {
    List<TransactionResponseDTO> getCreditTransactionsByTime(String username, TransactionRequest transactionRequest);

    List<TransactionResponseDTO> getCreditTransactionByID(String username, String id);

    List<TransactionResponseDTO> getCreditTransactionsByIDCustomer(String username, String idCustomer);

    List<TransactionResponseDTO> getDepositTransactionsByTime(String username, TransactionRequest transactionRequest);

    List<TransactionResponseDTO> getDepositTransactionByID(String username, String id);

    List<TransactionResponseDTO> getDepositTransactionsByIDCustomer(String username, String idCustomer);

    List<TransactionResponseDTO> getCreditTransactionByIDCustomerAndTime(String username, String idCustomer, TransactionRequest transactionRequest);

    List<TransactionResponseDTO> getDepositTransactionByIDCustomerAndTime(String username, String idCustomer, TransactionRequest transactionRequest);
}
