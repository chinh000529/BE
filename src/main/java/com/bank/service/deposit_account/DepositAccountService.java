package com.bank.service.deposit_account;

import com.bank.dto.request.DepositBankRequestDTO;
import com.bank.entity.DepositAccount;

import java.util.List;

public interface DepositAccountService {
    DepositAccount save(DepositBankRequestDTO depositBankRequestDTO);

    List<DepositBankRequestDTO> getAllDepositBankAccounts(String username);

    DepositBankRequestDTO getDepositBankById(String username, String id);

    DepositBankRequestDTO getDepositBankByIdCustomer(String username, String idCustomer);

    DepositBankRequestDTO updateDepositBankAccount(String username, DepositBankRequestDTO depositBankRequestDTO);
}
