package com.bank.service.employee;

import com.bank.dto.request.AccountRegistrationRequestDTO;
import com.bank.dto.response.PaycheckResponseDTO;
import com.bank.entity.Account;
import com.bank.entity.Employee;

import java.util.List;

public interface EmployeeService {
    Employee addEmployee(Employee employee, String username);
    Account registration(AccountRegistrationRequestDTO accountRegistrationRequestDTO);
    Employee getEmployeeByUsername(String username, String userSearching);
    List<PaycheckResponseDTO> getPayCheck(String username, Integer month, Integer year);
    List<PaycheckResponseDTO> getPaycheckOfEmployee(String username, String employeeId);

    Employee updateEmployee(String username, Employee employee);
    Employee getEmployeeById(String username, String employeeId);

    List<Employee> getAllEmployees(String username);

    List<PaycheckResponseDTO> getPaycheckByTimeAndEmployeeID(String username, int month, int year, String employeeId);
}
