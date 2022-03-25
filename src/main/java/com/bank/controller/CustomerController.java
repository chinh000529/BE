package com.bank.controller;

import com.bank.dto.request.*;
import com.bank.entity.Customer;
import com.bank.service.customer.CustomerService;
import com.bank.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value ="/${app.application-name}/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final JWTUtil jwtUtil;
    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<?> getAllCustomer(@RequestHeader("Authorization") String authorization){
        return ResponseEntity.ok(customerService.getAllCustomer(jwtUtil.extractUsername(authorization.substring(7))));
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getCustomerByUsername(@PathVariable("username") String username){
        return ResponseEntity.ok(customerService.getCustomerByUsername(username));
    }

    @PostMapping("/credit/registrations")
    public ResponseEntity<?> customerRegisterCredit(@RequestHeader("Authorization") String authorization, @RequestBody CreditBankRequestDTO creditBankRequestDTO){
        return ResponseEntity.ok(customerService.customerRegisterCredit(jwtUtil.extractUsername(authorization.substring(7)),creditBankRequestDTO));
    }

    @PostMapping("/deposit/registrations")
    public ResponseEntity<?> customerRegisterDeposit(@RequestHeader("Authorization") String authorization, @RequestBody DepositBankRequestDTO depositBankRequestDTO){
        return ResponseEntity.ok(customerService.customerRegisterDeposit(jwtUtil.extractUsername(authorization.substring(7)),depositBankRequestDTO));
    }

    @GetMapping("/balance/{id}")
    public ResponseEntity<?> getBalance(@PathVariable("id") String id){
        return ResponseEntity.ok(customerService.getBalance(id));
    }

    @PostMapping("/credit/pay-loan")
    public ResponseEntity<?> payCreditAccount(@RequestHeader("Authorization") String authorization, @RequestBody PaymentCreditAccount paymentCreditAccount){
        return ResponseEntity.ok(customerService.payDebtCredit(jwtUtil.extractUsername(authorization.substring(7)),paymentCreditAccount));
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseTransaction(@RequestHeader("Authorization") String authorization, @RequestBody PurchaseRequestDTO purchaseRequestDTO){
        return ResponseEntity.ok(customerService.purchaseTransaction(jwtUtil.extractUsername(authorization.substring(7)),purchaseRequestDTO));
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> rechargeDeposit(@RequestHeader("Authorization") String authorization, @RequestBody RechargeDepositRequestDTO rechargeDepositRequestDTO){
        return ResponseEntity.ok(customerService.rechargeDeposit(jwtUtil.extractUsername(authorization.substring(7)),rechargeDepositRequestDTO));
    }
    @PostMapping
    public ResponseEntity<?> addCustomer (@RequestHeader("Authorization") String authorization, @RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.addCustomer(jwtUtil.extractUsername(authorization.substring(7)), customer));
    }
    @GetMapping("/get-by-id/{customerId}")
    public ResponseEntity<?> getCustomerById(@RequestHeader("Authorization") String authorization, @PathVariable("customerId") String customerId){
        return ResponseEntity.ok(customerService.getCustomerById(jwtUtil.extractUsername(authorization.substring(7)),customerId));
    }
    @PutMapping
    public ResponseEntity<?> updateCustomer(@RequestHeader("Authorization") String authorization, @RequestBody Customer customer ) {
        return ResponseEntity.ok(customerService.updateCustomer(jwtUtil.extractUsername(authorization.substring(7)), customer));
    }
    @PostMapping("/registration")
    public ResponseEntity<?> customerRegistration(@RequestBody AccountRegistrationRequestDTO accountRegistrationRequestDTO) {
        return ResponseEntity.ok(customerService.customerRegistration(accountRegistrationRequestDTO));
    }
    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> deleteCustomer(@RequestHeader("Authorization") String authorization,@PathVariable("customerId") String customerId){
        return ResponseEntity.ok(customerService.deleteCustomer(jwtUtil.extractUsername(authorization.substring(7)),customerId));
    }
}
