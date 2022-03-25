package com.bank.dto.response;

import com.bank.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {
    private String id;
    private String username;
    private String role;
    private String email;
    public static AccountResponseDTO toAccountResponseDTO(Account account){
        return new AccountResponseDTO(account.getId(), account.getUsername(), account.getRole(), account.getEmail());
    }
}
