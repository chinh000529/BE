package com.bank.controller;

import com.bank.dto.request.AccountRegistrationRequestDTO;
import com.bank.entity.Employee;
import com.bank.service.employee.EmployeeService;
import com.bank.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value ="/${app.application-name}/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final JWTUtil jwtUtil;
    private final EmployeeService employeeService;

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody AccountRegistrationRequestDTO accountRegistrationRequestDTO){
        return ResponseEntity.ok(employeeService.registration(accountRegistrationRequestDTO));
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getEmployeeByUsername(@RequestHeader("Authorization") String authorization, @PathVariable("username") String usernameSearching){
        return ResponseEntity.ok(employeeService.getEmployeeByUsername(jwtUtil.extractUsername(authorization.substring(7)),usernameSearching));
    }

    @PostMapping
    public ResponseEntity<?> addEmployee(@RequestHeader("Authorization") String authorization, @RequestBody Employee employee){
        return ResponseEntity.ok(employeeService.addEmployee(employee,jwtUtil.extractUsername(authorization.substring(7))));
    }

    @GetMapping("/paycheck/{month}/{year}")
    public ResponseEntity<?> paycheck(@RequestHeader("Authorization") String authorization, @PathVariable("month") Integer month, @PathVariable("year") Integer year){
        return ResponseEntity.ok(employeeService.getPayCheck(jwtUtil.extractUsername(authorization.substring(7)),month,year));
    }

    @GetMapping("/paycheck/{employeeId}")
    public ResponseEntity<?> getPaycheckOfEmployee(@RequestHeader("Authorization") String authorization, @PathVariable("employeeId") String employeeId){
        return ResponseEntity.ok(employeeService.getPaycheckOfEmployee(jwtUtil.extractUsername(authorization.substring(7)),employeeId));
    }
    @GetMapping("/paycheck/{month}/{year}/{employeeId}")
    public ResponseEntity<?> getPaycheckByTimeAndEmployeeID(@RequestHeader("Authorization") String authorization,@PathVariable("month") int month, @PathVariable("year") int year, @PathVariable("employeeId") String employeeId){
        return ResponseEntity.ok(employeeService.getPaycheckByTimeAndEmployeeID(jwtUtil.extractUsername(authorization.substring(7)),month,year,employeeId));
    }

    @GetMapping("/get-by-id/{employeeId}")
    public ResponseEntity<?> getEmployeeById(@RequestHeader("Authorization") String authorization, @PathVariable("employeeId") String employeeId){
        return ResponseEntity.ok(employeeService.getEmployeeById(jwtUtil.extractUsername(authorization.substring(7)),employeeId));
    }
    @PutMapping()
    public ResponseEntity<?> updateEmployee(@RequestHeader("Authorization") String authorization, @RequestBody Employee employee ){
        return ResponseEntity.ok(employeeService.updateEmployee(jwtUtil.extractUsername(authorization.substring(7)),employee));
    }
    @GetMapping()
    public ResponseEntity<?> getAllEmployees(@RequestHeader("Authorization") String authorization) {
        return ResponseEntity.ok(employeeService.getAllEmployees(jwtUtil.extractUsername(authorization.substring(7))));
    }
}
