package com.bank.service.account;

import com.bank.dto.response.AccountResponseDTO;
import com.bank.entity.Account;

import java.util.List;

public interface AccountService {
    List<AccountResponseDTO> findAll(String username);

    AccountResponseDTO getAccountByUsername(String username, String usernameSearching);

    Account deleteAccountByUsername(String username, String usernameDeleting);
}
