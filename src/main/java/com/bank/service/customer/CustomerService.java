package com.bank.service.customer;

import com.bank.dto.request.*;
import com.bank.dto.response.BalanceResponseDTO;
import com.bank.entity.Account;
import com.bank.entity.BankAccount;
import com.bank.entity.Customer;
import com.bank.entity.Transaction;

import java.util.List;

public interface CustomerService {
    Customer getCustomerByUsername(String username);
    BalanceResponseDTO getBalance(String id);
    BankAccount customerRegisterCredit(String username, CreditBankRequestDTO creditBankRequestDTO);
    BankAccount customerRegisterDeposit(String username, DepositBankRequestDTO depositBankRequestDTO);
    Transaction payDebtCredit(String username, PaymentCreditAccount paymentCreditAccount);
    Transaction purchaseTransaction(String username, PurchaseRequestDTO purchaseRequestDTO);
    Transaction rechargeDeposit(String username, RechargeDepositRequestDTO rechargeDepositRequestDTO);
    Customer addCustomer(String username, Customer customer);
    List<Customer> getAllCustomer(String username);
    Customer getCustomerById(String username, String customerId);
    Customer updateCustomer(String username, Customer customer);
    Account customerRegistration(AccountRegistrationRequestDTO accountRegistrationRequestDTO);
    Customer deleteCustomer(String username, String customerId);
}
