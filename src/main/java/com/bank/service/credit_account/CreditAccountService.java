package com.bank.service.credit_account;

import com.bank.dto.request.CreditBankRequestDTO;
import com.bank.entity.CreditAccount;

import java.util.List;

public interface CreditAccountService {
    CreditAccount save(CreditBankRequestDTO creditBankRequestDTO);

    List<CreditBankRequestDTO> getAllCreditBankAccounts(String username);

    CreditBankRequestDTO getCreditBankById(String username, String id);

    CreditBankRequestDTO getCreditBankByIdCustomer(String username, String idCustomer);

    CreditBankRequestDTO updateCreditBankAccount(String username, CreditBankRequestDTO creditBankRequestDTO);
}
