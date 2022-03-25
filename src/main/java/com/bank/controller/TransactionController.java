package com.bank.controller;

import com.bank.dto.request.TransactionRequest;
import com.bank.service.transaction.TransactionService;
import com.bank.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value ="/${app.application-name}/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final JWTUtil jwtUtil;
    private final TransactionService transactionService;

    @PostMapping("/credit")
    public ResponseEntity<?> getCreditTransactionsByTime(@RequestHeader("Authorization") String authorization,@RequestBody TransactionRequest transactionRequest){
        return ResponseEntity.ok(transactionService.getCreditTransactionsByTime(jwtUtil.extractUsername(authorization.substring(7)),transactionRequest));
    }

    @GetMapping("/credit/{id}")
    public ResponseEntity<?> getCreditTransactionByID(@RequestHeader("Authorization") String authorization,@PathVariable("id") String id){
        return ResponseEntity.ok(transactionService.getCreditTransactionByID(jwtUtil.extractUsername(authorization.substring(7)),id));
    }

    @GetMapping("/credit/customers/{idCustomer}")
    public ResponseEntity<?> getCreditTransactionsByIDCustomer(@RequestHeader("Authorization") String authorization,@PathVariable("idCustomer") String idCustomer){
        return ResponseEntity.ok(transactionService.getCreditTransactionsByIDCustomer(jwtUtil.extractUsername(authorization.substring(7)),idCustomer));
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> getDepositTransactionsByTime(@RequestHeader("Authorization") String authorization,@RequestBody TransactionRequest transactionRequest){
        return ResponseEntity.ok(transactionService.getDepositTransactionsByTime(jwtUtil.extractUsername(authorization.substring(7)),transactionRequest));
    }

    @GetMapping("/deposit/{id}")
    public ResponseEntity<?> getDepositTransactionByID(@RequestHeader("Authorization") String authorization,@PathVariable("id") String id){
        return ResponseEntity.ok(transactionService.getDepositTransactionByID(jwtUtil.extractUsername(authorization.substring(7)),id));
    }

    @GetMapping("/deposit/customers/{idCustomer}")
    public ResponseEntity<?> getDepositTransactionsByIDCustomer(@RequestHeader("Authorization") String authorization,@PathVariable("idCustomer") String idCustomer){
        return ResponseEntity.ok(transactionService.getDepositTransactionsByIDCustomer(jwtUtil.extractUsername(authorization.substring(7)),idCustomer));
    }

    @PostMapping("/credit/customers/{idCustomer}/time")
    public ResponseEntity<?> getCreditTransactionByIDCustomerAndTime(@RequestHeader("Authorization") String authorization,@PathVariable("idCustomer") String idCustomer,@RequestBody TransactionRequest transactionRequest){
        return ResponseEntity.ok(transactionService.getCreditTransactionByIDCustomerAndTime(jwtUtil.extractUsername(authorization.substring(7)),idCustomer,transactionRequest));
    }
    @PostMapping("/deposit/customers/{idCustomer}/time")
    public ResponseEntity<?> getDepositTransactionByIDCustomerAndTime(@RequestHeader("Authorization") String authorization,@PathVariable("idCustomer") String idCustomer,@RequestBody TransactionRequest transactionRequest){
        return ResponseEntity.ok(transactionService.getDepositTransactionByIDCustomerAndTime(jwtUtil.extractUsername(authorization.substring(7)),idCustomer,transactionRequest));
    }
}
