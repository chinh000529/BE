package com.bank.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRegistrationRequestDTO {
    private String infoId;
    private String username;
    private String password;
    private String email;
}
