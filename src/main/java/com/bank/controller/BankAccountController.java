package com.bank.controller;

import com.bank.dto.request.CreditBankRequestDTO;
import com.bank.dto.request.DepositBankRequestDTO;
import com.bank.service.credit_account.CreditAccountService;
import com.bank.service.deposit_account.DepositAccountService;
import com.bank.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value ="/${app.application-name}")
@RequiredArgsConstructor
public class BankAccountController {
    private final JWTUtil jwtUtil;
    private final DepositAccountService depositAccountService;
    private final CreditAccountService creditAccountService;

    @GetMapping("/deposit-bank-accounts")
    public ResponseEntity<?> getAllDepositBankAccounts(@RequestHeader("Authorization") String authorization){
        return ResponseEntity.ok(depositAccountService.getAllDepositBankAccounts(jwtUtil.extractUsername(authorization.substring(7))));
    }

    @GetMapping("/credit-bank-accounts")
    public ResponseEntity<?> getAllCreditBankAccounts(@RequestHeader("Authorization") String authorization){
        return ResponseEntity.ok(creditAccountService.getAllCreditBankAccounts(jwtUtil.extractUsername(authorization.substring(7))));
    }

    @GetMapping("/deposit-bank-accounts/{id}")
    public ResponseEntity<?> getDepositBankById(@RequestHeader("Authorization") String authorization, @PathVariable("id") String id){
        return ResponseEntity.ok(depositAccountService.getDepositBankById(jwtUtil.extractUsername(authorization.substring(7)), id));
    }

    @GetMapping("/credit-bank-accounts/{id}")
    public ResponseEntity<?> getCreditBankById(@RequestHeader("Authorization") String authorization, @PathVariable("id") String id){
        return ResponseEntity.ok(creditAccountService.getCreditBankById(jwtUtil.extractUsername(authorization.substring(7)), id));
    }

    @GetMapping("/deposit-bank-accounts/customer/{idCustomer}")
    public ResponseEntity<?> getDepositBankByIdCustomer(@RequestHeader("Authorization") String authorization, @PathVariable("idCustomer") String idCustomer){
        return ResponseEntity.ok(depositAccountService.getDepositBankByIdCustomer(jwtUtil.extractUsername(authorization.substring(7)), idCustomer));
    }

    @GetMapping("/credit-bank-accounts/customer/{idCustomer}")
    public ResponseEntity<?> getCreditBankByIdCustomer(@RequestHeader("Authorization") String authorization, @PathVariable("idCustomer") String idCustomer){
        return ResponseEntity.ok(creditAccountService.getCreditBankByIdCustomer(jwtUtil.extractUsername(authorization.substring(7)), idCustomer));
    }

    @PutMapping("/deposit-bank-accounts")
    public ResponseEntity<?> updateDepositBankAccount(@RequestHeader("Authorization") String authorization, @RequestBody DepositBankRequestDTO depositBankRequestDTO){
        return ResponseEntity.ok(depositAccountService.updateDepositBankAccount(jwtUtil.extractUsername(authorization.substring(7)), depositBankRequestDTO));
    }

    @PutMapping("/credit-bank-accounts")
    public ResponseEntity<?> updateCreditBankAccount(@RequestHeader("Authorization") String authorization, @RequestBody CreditBankRequestDTO creditBankRequestDTO){
        return ResponseEntity.ok(creditAccountService.updateCreditBankAccount(jwtUtil.extractUsername(authorization.substring(7)), creditBankRequestDTO));
    }
}
