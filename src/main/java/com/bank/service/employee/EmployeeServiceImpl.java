package com.bank.service.employee;

import com.bank.constant.ErrorMessage;
import com.bank.constant.RoleAccountConstant;
import com.bank.constant.TypeOfBankCard;
import com.bank.dto.request.AccountRegistrationRequestDTO;
import com.bank.dto.response.PaycheckResponseDTO;
import com.bank.entity.*;
import com.bank.exception.BadRequestException;
import com.bank.repository.*;
import com.bank.utils.UILD;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepo employeeRepo;
    private final AccountRepo accountRepo;
    private final PaycheckRepo paycheckRepo;
    private final BankAccountRepo bankAccountRepo;
    private final DepositAccountRepo depositAccountRepo;

    @Override
    public Employee addEmployee(Employee employee, String username) {
        if(!accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_ADMIN).isPresent())
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        if(employeeRepo.findEmployeeById(employee.getId()).isPresent())
            throw new BadRequestException(ErrorMessage.MA_NHAN_VIEN_DA_TON_TAI);

        employee.setAccountId("NO");
        return employeeRepo.save(employee);
    }

    @Override
    public Account registration(AccountRegistrationRequestDTO accountRegistrationRequestDTO) {
        Optional<Employee> employee = employeeRepo.findEmployeeById(accountRegistrationRequestDTO.getInfoId());
        // kiem tra nhan vien co ton tai hay khong
        if (!employee.isPresent())
            throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_NHAN_VIEN);

        // nhaan vien chua dang ky tai khoan
        if (!"NO".equalsIgnoreCase(employee.get().getAccountId()))
            throw new BadRequestException(ErrorMessage.NHAN_VIEN_DA_CO_TAI_KHOAN);

        if (accountRepo.findAccountByUsername(accountRegistrationRequestDTO.getUsername()).isPresent())
            throw new BadRequestException(ErrorMessage.TEN_TAI_KHOAN_DA_TON_TAI);

        // them account vao he thong
        Account account = new Account();
        account.setId(UILD.nextULID());
        account.setUsername(accountRegistrationRequestDTO.getUsername());
        account.setPassword(accountRegistrationRequestDTO.getPassword());
        account.setRole(RoleAccountConstant.ROLE_EMPLOYEE);
        account.setEmail(accountRegistrationRequestDTO.getEmail());
        accountRepo.save(account);

        // luu lai account_id vao bang employee
        employee.get().setAccountId(account.getId());
        return account;
    }

    @Override
    public Employee getEmployeeByUsername(String username, String userSearching) {
        Account accountLogged = accountRepo.findAccountByUsername(username).get();
        if(!(accountLogged.getRole().equalsIgnoreCase(RoleAccountConstant.ROLE_ADMIN) || accountLogged.getRole().equalsIgnoreCase(RoleAccountConstant.ROLE_EMPLOYEE)))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        if(accountLogged.getRole().equalsIgnoreCase(RoleAccountConstant.ROLE_EMPLOYEE) && !username.equals(userSearching))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        Optional<Account> accountSearching = accountRepo.findAccountByUsername(userSearching);
        if(!accountSearching.isPresent()) throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_ACCOUNT);

        Optional<Employee> employee= employeeRepo.findEmployeeByAccountId(accountSearching.get().getId());
        if(!employee.isPresent()) throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_NHAN_VIEN);

        return employee.get();
    }

    @Override
    public List<PaycheckResponseDTO> getPayCheck(String username, Integer month, Integer year) {
        if(!accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_ADMIN).isPresent())
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        LocalDate localDate = LocalDate.now();
        int currentMonth = localDate.getMonthValue();
        int currentYear = localDate.getYear();

        if(year > currentYear || (year == currentYear && month >= currentMonth))
            throw new BadRequestException(ErrorMessage.KHONG_THE_XUAT_PHIEU_LUONG_TRUOC);

        // tìm xem tháng, năm muốn xuất phiếu lương đã được xuất trước đó hay chưa
        List<Paycheck> paychecks =  paycheckRepo.findAllByMonthAndYear(month,year);
        List<PaycheckResponseDTO> paycheckResponseDTOS = new ArrayList<>();
        if(!paychecks.isEmpty()){
            // add thêm thông tin
            for(Paycheck paycheck:paychecks){
                paycheckResponseDTOS.add(toPaycheckResponse(paycheck));
            }
        }else{
            // lap qua tung nhan vien
            List<Employee> employees = employeeRepo.findAll();
            List<BankAccount> bankAccounts = bankAccountRepo.findAll();
            for(Employee employee:employees){
                Double amount = employee.getMonthlySalary();

                // lap qua danh sach tk tin dung
                // chua tinh den TH tai khoan tin dung co duoc tao o trong thang day hay ko
                for(BankAccount bankAccount:bankAccounts){
                    LocalDate createdDate = bankAccount.getCreatedDate().toLocalDate();
                    if(bankAccount.getType().equalsIgnoreCase(TypeOfBankCard.CREDIT) && employee.getId().equalsIgnoreCase(bankAccount.getEmployeeId()) && month == createdDate.getMonthValue() && year == createdDate.getYear())
                        amount += 500000;
                    if(bankAccount.getType().equalsIgnoreCase(TypeOfBankCard.DEPOSIT) && employee.getId().equalsIgnoreCase(bankAccount.getEmployeeId())){
                        DepositAccount depositAccount = depositAccountRepo.findDepositAccountById(bankAccount.getId()).get();
                        Date firstDepositDate = depositAccount.getFirstDepositDate();
                        if(firstDepositDate != null){
                            LocalDate firstDep = firstDepositDate.toLocalDate();
                            if(firstDep.getYear() == year && firstDep.getMonthValue() == month) amount += (depositAccount.getFirstRecharge() * 2 / 100);
                        }
                    }
                }

                Paycheck paycheck = new Paycheck();
                paycheck.setId(UILD.nextULID());
                paycheck.setEmployeeId(employee.getId());
                paycheck.setAmount(amount);
                paycheck.setMonthlySalary(employee.getMonthlySalary());
                paycheck.setMonth(month);
                paycheck.setYear(year);
                paycheck.setCreatedDate(new Date(System.currentTimeMillis()));

                paycheckRepo.save(paycheck);

                paycheckResponseDTOS.add(toPaycheckResponse(paycheck));
            }
        }

        return paycheckResponseDTOS;
    }

    @Override
    public List<PaycheckResponseDTO> getPaycheckOfEmployee(String username, String employeeId) {
        if(!(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_ADMIN).isPresent() || accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_EMPLOYEE).isPresent()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        Optional<Employee> employee = employeeRepo.findEmployeeById(employeeId);
        if(!employee.isPresent())
            throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_NHAN_VIEN);

        Account account = accountRepo.findAccountByUsername(username).get();
        if(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_EMPLOYEE).isPresent() && !account.getId().equalsIgnoreCase(employee.get().getAccountId()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        List<Paycheck> paychecks = paycheckRepo.findAllByEmployeeId(employeeId);
        if(paychecks.isEmpty()) throw new BadRequestException(ErrorMessage.NHAN_VIEN_KHONG_CO_PHIEU_LUONG);

        List<PaycheckResponseDTO> paycheckResponseDTOS = new ArrayList<>();
        for(Paycheck paycheck:paychecks){
            paycheckResponseDTOS.add(toPaycheckResponse(paycheck));
        }
        return paycheckResponseDTOS;
    }

    @Override
    public Employee updateEmployee(String username, Employee employee) {
        if(!(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_ADMIN).isPresent() || accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_EMPLOYEE).isPresent()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        Optional<Account> account = accountRepo.findAccountById(employee.getAccountId());
        if(account.isPresent()){
            if(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_EMPLOYEE).isPresent() && !account.get().getUsername().equalsIgnoreCase(username))
                throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);
        }

        Optional<Employee> oldEmployee = employeeRepo.findEmployeeById(employee.getId());
        if(!oldEmployee.isPresent())
            throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_NHAN_VIEN);

        oldEmployee.get().setAccountId(employee.getAccountId());
        oldEmployee.get().setAddress(employee.getAddress());
        oldEmployee.get().setDateOfBirth(employee.getDateOfBirth());
        oldEmployee.get().setMonthlySalary(employee.getMonthlySalary());
        oldEmployee.get().setFullName(employee.getFullName());
        oldEmployee.get().setJobLevel(employee.getJobLevel());
        oldEmployee.get().setIdCard(employee.getIdCard());
        oldEmployee.get().setPhoneNumber(employee.getPhoneNumber());
        oldEmployee.get().setPosition(employee.getPosition());
        oldEmployee.get().setSeniority(employee.getSeniority());

        return oldEmployee.get();
    }

    @Override
    public Employee getEmployeeById(String username, String employeeId) {
        Account account = accountRepo.findAccountByUsername(username).get();
        if(!(account.getRole().equalsIgnoreCase(RoleAccountConstant.ROLE_ADMIN) || account.getRole().equalsIgnoreCase(RoleAccountConstant.ROLE_EMPLOYEE)))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);
        Optional<Employee> employee= employeeRepo.findEmployeeById(employeeId);
        if(!employee.isPresent()) throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_NHAN_VIEN);

        if(account.getRole().equalsIgnoreCase(RoleAccountConstant.ROLE_EMPLOYEE) && !account.getId().equalsIgnoreCase(employee.get().getAccountId()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        return employee.get();
    }

    @Override
    public List<Employee> getAllEmployees(String username) {
        if(!accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_ADMIN).isPresent())
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);
        return employeeRepo.findAll();
    }

    @Override
    public List<PaycheckResponseDTO> getPaycheckByTimeAndEmployeeID(String username, int month, int year, String employeeId) {
        if(!(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_ADMIN).isPresent() || accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_EMPLOYEE).isPresent()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        Optional<Employee> employee = employeeRepo.findEmployeeById(employeeId);
        if(!employee.isPresent())
            throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_NHAN_VIEN);

        Account account = accountRepo.findAccountByUsername(username).get();
        if(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_EMPLOYEE).isPresent() && !account.getId().equalsIgnoreCase(employee.get().getAccountId()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        List<Paycheck> paychecks = paycheckRepo.findAllByMonthAndYearAndEmployeeId(month,year,employeeId);
        if(paychecks.isEmpty()) throw new BadRequestException(ErrorMessage.NHAN_VIEN_KHONG_CO_PHIEU_LUONG);

        List<PaycheckResponseDTO> paycheckResponseDTOS = new ArrayList<>();
        for(Paycheck paycheck:paychecks){
            paycheckResponseDTOS.add(toPaycheckResponse(paycheck));
        }
        return paycheckResponseDTOS;
    }

    public PaycheckResponseDTO toPaycheckResponse(Paycheck paycheck){
        PaycheckResponseDTO paycheckResponseDTO = new PaycheckResponseDTO();
        paycheckResponseDTO.setPaycheckId(paycheck.getId());
        paycheckResponseDTO.setEmployeeId(paycheck.getEmployeeId());
        paycheckResponseDTO.setEmployeeName(employeeRepo.findEmployeeById(paycheck.getEmployeeId()).get().getFullName());
        paycheckResponseDTO.setAmount(paycheck.getAmount());
        paycheckResponseDTO.setMonthlySalary(paycheck.getMonthlySalary());
        paycheckResponseDTO.setCreatedDate(new Date(System.currentTimeMillis()));
        paycheckResponseDTO.setMonth(paycheck.getMonth());
        paycheckResponseDTO.setYear(paycheck.getYear());

        return paycheckResponseDTO;
    }
}
