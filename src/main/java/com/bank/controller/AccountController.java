package com.bank.controller;

import com.bank.service.account.AccountService;
import com.bank.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value ="/${app.application-name}/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final JWTUtil jwtUtil;

    @GetMapping()
    public ResponseEntity<?> findAll(@RequestHeader("Authorization") String authorization){
        return ResponseEntity.ok(accountService.findAll(jwtUtil.extractUsername(authorization.substring(7))));
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getAccountByUsername(@RequestHeader("Authorization") String authorization, @PathVariable("username") String username) {
        return ResponseEntity.ok(accountService.getAccountByUsername(jwtUtil.extractUsername(authorization.substring(7)), username));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteAccountByUsername(@RequestHeader("Authorization") String authorization, @PathVariable("username") String username) {
        return  ResponseEntity.ok(accountService.deleteAccountByUsername(jwtUtil.extractUsername(authorization.substring(7)), username));
    }
}
