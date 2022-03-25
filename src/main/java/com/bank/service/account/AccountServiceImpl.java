package com.bank.service.account;

import com.bank.constant.ErrorMessage;
import com.bank.constant.RoleAccountConstant;
import com.bank.dto.response.AccountResponseDTO;
import com.bank.entity.Account;
import com.bank.entity.Customer;
import com.bank.entity.Employee;
import com.bank.exception.BadRequestException;
import com.bank.repository.AccountRepo;
import com.bank.repository.CustomerRepo;
import com.bank.repository.EmployeeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {
    private final AccountRepo accountRepo;
    private final EmployeeRepo employeeRepo;
    private final CustomerRepo customerRepo;

    @Override
    public List<AccountResponseDTO> findAll(String username) {
        if(!(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_ADMIN).isPresent() || accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_EMPLOYEE).isPresent()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        List<Account> accounts = accountRepo.findAll();
        List<AccountResponseDTO> accountResponseDTOs = new ArrayList<>();

        for(Account account:accounts){
            accountResponseDTOs.add(AccountResponseDTO.toAccountResponseDTO(account));
        }
        return accountResponseDTOs;
    }

    @Override
    public AccountResponseDTO getAccountByUsername(String username, String usernameSearching) {
        Optional<Account> account = accountRepo.findAccountByUsername(usernameSearching);
        if(!account.isPresent())
            throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_ACCOUNT);
        return AccountResponseDTO.toAccountResponseDTO(account.get());
    }

    @Override
    public Account deleteAccountByUsername(String username, String usernameDeleting) {
        Optional<Account> accountDeleting = accountRepo.findAccountByUsername(usernameDeleting);
        Optional<Account> account = accountRepo.findAccountByUsername(username);

        if(!accountDeleting.isPresent()) throw new BadRequestException(ErrorMessage.ACCOUNT_DELETE_KHONG_TON_TAI);

        if(accountDeleting.get().getRole().equalsIgnoreCase(RoleAccountConstant.ROLE_ADMIN))
            throw new BadRequestException(ErrorMessage.KHONG_THE_XOA_ACCOUNT_ADMIN);

        if(accountDeleting.get().getRole().equalsIgnoreCase(RoleAccountConstant.ROLE_EMPLOYEE)){
            if(account.get().getRole().equalsIgnoreCase(RoleAccountConstant.ROLE_ADMIN)){
                // tieens hanh xoas
                deleteAccountEmployee(accountDeleting.get());
            }else if(!account.get().getRole().equalsIgnoreCase(RoleAccountConstant.ROLE_EMPLOYEE)){
                throw new BadRequestException(ErrorMessage.KHONG_THE_XOA_ACCOUNT);
            }
            if(account.get().getRole().equalsIgnoreCase(RoleAccountConstant.ROLE_EMPLOYEE) && !username.equalsIgnoreCase(usernameDeleting)){
                throw new BadRequestException(ErrorMessage.KHONG_THE_XOA_ACCOUNT_EMPLOYEE);
            }

            if(account.get().getRole().equalsIgnoreCase(RoleAccountConstant.ROLE_EMPLOYEE) && username.equalsIgnoreCase(usernameDeleting)){
                deleteAccountEmployee(accountDeleting.get());
            }

            return null;
        }

        // luc nay tk muon xoa la CUSTOMER
        if(account.get().getRole().equalsIgnoreCase(RoleAccountConstant.ROLE_CUSTOMER)
            && !username.equalsIgnoreCase(usernameDeleting))
            throw new BadRequestException(ErrorMessage.KHONG_THE_XOA_ACCOUNT_CUSTOMER);

        // xoa tk khach hang kh
        Customer customer = customerRepo.findCustomerByAccountId(accountDeleting.get().getId()).get();
        customer.setAccountId("NO");
        accountRepo.delete(accountDeleting.get());

        return null;
    }

    public void deleteAccountEmployee(Account accountDeleting){
        Employee employee = employeeRepo.findEmployeeByAccountId(accountDeleting.getId()).get();
        employee.setAccountId("NO");

        accountRepo.delete(accountDeleting);
    }
}
