package com.bank.service.customer;

import com.bank.constant.ErrorMessage;
import com.bank.constant.RoleAccountConstant;
import com.bank.constant.TypeOfBankCard;
import com.bank.constant.TypeOfTransaction;
import com.bank.dto.request.*;
import com.bank.dto.response.BalanceResponseDTO;
import com.bank.entity.*;
import com.bank.exception.BadRequestException;
import com.bank.repository.*;
import com.bank.utils.UILD;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepo customerRepo;
    private final AccountRepo accountRepo;
    private final BankAccountRepo bankAccountRepo;
    private final CreditAccountRepo creditAccountRepo;
    private final DepositAccountRepo depositAccountRepo;
    private final TransactionRepo transactionRepo;

    @Override
    public Customer getCustomerByUsername(String username) {
        Optional<Account> account = accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_CUSTOMER);
        if(account.isPresent()) return customerRepo.findCustomerByAccountId(account.get().getId()).get();

        throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_KHACH_HANG);
    }

    @Override
    public BalanceResponseDTO getBalance(String id) {
        Optional<BankAccount> bankAccount = bankAccountRepo.findBankAccountById(id);
        if(!bankAccount.isPresent())
            throw new BadRequestException(ErrorMessage.SO_TAI_KHOAN_KHONG_TON_TAI);

        return new BalanceResponseDTO(bankAccount.get().getBalance());
    }

    @Override
    public BankAccount customerRegisterCredit(String username, CreditBankRequestDTO creditBankRequestDTO) {
        if(!(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_ADMIN).isPresent() || accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_EMPLOYEE).isPresent()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        if(!customerRepo.findCustomerById(creditBankRequestDTO.getCustomerId()).isPresent())
            throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_KHACH_HANG);

        if(bankAccountRepo.findBankAccountByCustomerIdAndType(creditBankRequestDTO.getCustomerId(), TypeOfBankCard.CREDIT).isPresent())
            throw new BadRequestException(ErrorMessage.KHACH_HANG_DA_CO_TAI_KHOAN_TIN_DUNG);

        if(bankAccountRepo.findBankAccountById(creditBankRequestDTO.getId()).isPresent())
            throw new BadRequestException(ErrorMessage.SO_TAI_KHOAN_DA_TON_TAI);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(creditBankRequestDTO.getId());
        bankAccount.setBalance(creditBankRequestDTO.getBalance());
        bankAccount.setInterestRate(creditBankRequestDTO.getInterestRate());
        bankAccount.setCreatedDate(creditBankRequestDTO.getCreatedDate());
        bankAccount.setType(TypeOfBankCard.CREDIT);
        bankAccount.setCustomerId(creditBankRequestDTO.getCustomerId());
        bankAccount.setEmployeeId(creditBankRequestDTO.getEmployeeId());

        creditAccountRepo.save(new CreditAccount(creditBankRequestDTO.getId(),creditBankRequestDTO.getMaxLoan(), creditBankRequestDTO.getExpirationDate()));

        return bankAccountRepo.save(bankAccount);
    }

    @Override
    public BankAccount customerRegisterDeposit(String username, DepositBankRequestDTO depositBankRequestDTO) {
        if(!(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_ADMIN).isPresent() || accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_EMPLOYEE).isPresent()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        if(!customerRepo.findCustomerById(depositBankRequestDTO.getCustomerId()).isPresent())
            throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_KHACH_HANG);

        if(bankAccountRepo.findBankAccountByCustomerIdAndType(depositBankRequestDTO.getCustomerId(), TypeOfBankCard.DEPOSIT).isPresent())
            throw new BadRequestException(ErrorMessage.KHACH_HANG_DA_CO_TAI_KHOAN_GUI_TIEN);

        if(bankAccountRepo.findBankAccountById(depositBankRequestDTO.getId()).isPresent())
            throw new BadRequestException(ErrorMessage.SO_TAI_KHOAN_DA_TON_TAI);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(depositBankRequestDTO.getId());
        bankAccount.setBalance(depositBankRequestDTO.getBalance());
        bankAccount.setInterestRate(depositBankRequestDTO.getInterestRate());
        bankAccount.setCreatedDate(depositBankRequestDTO.getCreatedDate());
        bankAccount.setType(TypeOfBankCard.DEPOSIT);
        bankAccount.setCustomerId(depositBankRequestDTO.getCustomerId());
        bankAccount.setEmployeeId(depositBankRequestDTO.getEmployeeId());

        depositAccountRepo.save(
                new DepositAccount(depositBankRequestDTO.getId(), depositBankRequestDTO.getMinBalance(), depositBankRequestDTO.getFirstRecharge(),depositBankRequestDTO.getFirstDepositDate()));

        return bankAccountRepo.save(bankAccount);
    }

    @Override
    public Transaction payDebtCredit(String username, PaymentCreditAccount paymentCreditAccount) {
        // Kiểm tra xem tính tồn tại và có đùng là tài khoản gửi tiền hay không
        if(!bankAccountRepo.findBankAccountById(paymentCreditAccount.getDepositAccountId()).isPresent())
            throw new BadRequestException(ErrorMessage.TAI_KHOAN_GUI_TIEN_KHONG_TON_TAI);
        BankAccount depositAccount = bankAccountRepo.findBankAccountById(paymentCreditAccount.getDepositAccountId()).get();

        if(!bankAccountRepo.findBankAccountById(paymentCreditAccount.getCreditAccountId()).isPresent())
            throw new BadRequestException(ErrorMessage.TAI_KHOAN_TIN_DUNG_KHONG_TON_TAI);
        BankAccount creditAccount = bankAccountRepo.findBankAccountById(paymentCreditAccount.getCreditAccountId()).get();

        if(!TypeOfBankCard.DEPOSIT.equalsIgnoreCase(depositAccount.getType()))
            throw new BadRequestException(ErrorMessage.KHONG_PHAI_TAI_KHOAN_GUI_TIEN);

        // kiểm tra xem tài khoản gửi tiền có phải của khách hàng đang đăng nhập hệ thống không
        Account account = accountRepo.findAccountByUsername(username).get();
        if(RoleAccountConstant.ROLE_CUSTOMER.equalsIgnoreCase(account.getRole()) &&
                !depositAccount.getCustomerId().equalsIgnoreCase(customerRepo.findCustomerByAccountId(account.getId()).get().getId()))
            throw new BadRequestException(ErrorMessage.TAI_KHOAN_GUI_TIEN_KHONG_PHAI_CUA_KHACH_HANG);

        // kiểm tra tài khoản đích có phải là 1 tài khoản tín dụng hay không
        if(!TypeOfBankCard.CREDIT.equalsIgnoreCase(creditAccount.getType()))
            throw new BadRequestException(ErrorMessage.TAI_KHOAN_DICH_KHONG_PHAI_LA_TAI_KHOAN_TIN_DUNG);

        // kiểm tra xem số dư tài khoản thoả mãn được transaction ko
        DepositAccount deposit = depositAccountRepo.findDepositAccountById(depositAccount.getId()).get();
        if(depositAccount.getBalance() - paymentCreditAccount.getMoney() < deposit.getMinBalance())
            throw new BadRequestException(ErrorMessage.SO_DU_TAI_KHOAN_GUI_TIEN_KHONG_DU);

        // cộng trừ tiền
        depositAccount.setBalance(depositAccount.getBalance() - paymentCreditAccount.getMoney());
        creditAccount.setBalance(creditAccount.getBalance() + paymentCreditAccount.getMoney());

        // luu transaction
        Transaction transaction = new Transaction();
        transaction.setId(UILD.nextULID());
        transaction.setIdBankSource(paymentCreditAccount.getDepositAccountId());
        transaction.setIdBankTarget(paymentCreditAccount.getCreditAccountId());
        transaction.setTransactionAmount(paymentCreditAccount.getMoney());
        transaction.setType(TypeOfTransaction.THANH_TOAN_SO_NO_TIN_DUNG);
        transaction.setContent(paymentCreditAccount.getContent());
        transaction.setTransactionDate(new java.sql.Date(System.currentTimeMillis()));

        return transactionRepo.save(transaction);
    }

    @Override
    public Transaction purchaseTransaction(String username, PurchaseRequestDTO purchaseRequestDTO) {
        Account account = accountRepo.findAccountByUsername(username).get();
        if(!RoleAccountConstant.ROLE_CUSTOMER.equalsIgnoreCase(account.getRole()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        if(!bankAccountRepo.findBankAccountById(purchaseRequestDTO.getSourceBankId()).isPresent())
            throw new BadRequestException(ErrorMessage.TAI_KHOAN_NGUON_KHONG_TON_TAI);
        BankAccount sourceBankAccount = bankAccountRepo.findBankAccountById(purchaseRequestDTO.getSourceBankId()).get();

        if(!bankAccountRepo.findBankAccountById(purchaseRequestDTO.getTargetBankId()).isPresent())
            throw new BadRequestException(ErrorMessage.TAI_KHOAN_DICH_KHONG_TON_TAI);
        BankAccount targetBankAccount = bankAccountRepo.findBankAccountById(purchaseRequestDTO.getTargetBankId()).get();

        if(!TypeOfBankCard.CREDIT.equalsIgnoreCase(sourceBankAccount.getType()))
            throw new BadRequestException(ErrorMessage.TAI_KHOAN_NGUON_KHONG_PHAI_TAI_KHOAN_TIN_DUNG);

        Customer customer = customerRepo.findCustomerByAccountId(account.getId()).get();
        if(!sourceBankAccount.getCustomerId().equalsIgnoreCase(customer.getId()))
            throw new BadRequestException(ErrorMessage.TAI_KHOAN_NGUON_KHONG_PHAI_CUA_KHACH_HANG);

        CreditAccount creditAccount = creditAccountRepo.findCreditAccountById(sourceBankAccount.getId()).get();
        Double result = sourceBankAccount.getBalance() - purchaseRequestDTO.getMoney();

        if(result < 0 && Math.abs(result) > creditAccount.getMaxLoan())
            throw new BadRequestException(ErrorMessage.SO_DU_TAI_KHOAN_TIN_DUNG_KHONG_DU);

        sourceBankAccount.setBalance(result);
        targetBankAccount.setBalance(targetBankAccount.getBalance() + purchaseRequestDTO.getMoney());

        // luu transaction
        Transaction transaction = new Transaction();
        transaction.setId(UILD.nextULID());
        transaction.setIdBankSource(purchaseRequestDTO.getSourceBankId());
        transaction.setIdBankTarget(purchaseRequestDTO.getTargetBankId());
        transaction.setTransactionAmount(purchaseRequestDTO.getMoney());
        transaction.setType(TypeOfTransaction.THANH_TOAN_MUA_HANG);
        transaction.setContent(purchaseRequestDTO.getContent());
        transaction.setTransactionDate(new java.sql.Date(System.currentTimeMillis()));

        return transactionRepo.save(transaction);
    }

    @Override
    public Transaction rechargeDeposit(String username, RechargeDepositRequestDTO rechargeDepositRequestDTO) {
        if(!(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_ADMIN).isPresent() || accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_EMPLOYEE).isPresent()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        if(!bankAccountRepo.findBankAccountById(rechargeDepositRequestDTO.getTargetBankId()).isPresent())
            throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_TAI_KHOAN);
        BankAccount targetBankAccount = bankAccountRepo.findBankAccountById(rechargeDepositRequestDTO.getTargetBankId()).get();

        if(!targetBankAccount.getType().equalsIgnoreCase(TypeOfBankCard.DEPOSIT))
            throw new BadRequestException(ErrorMessage.TAI_KHOAN_DICH_KHONG_PHAI_LA_TAI_KHOAN_GUI_TIEN);

        DepositAccount depositAccount = depositAccountRepo.findDepositAccountById(targetBankAccount.getId()).get();
        if(depositAccount.getFirstRecharge() == null || depositAccount.getFirstRecharge() == 0){
            depositAccount.setFirstRecharge(rechargeDepositRequestDTO.getMoney());
            depositAccount.setFirstDepositDate(new Date(System.currentTimeMillis()));
        }
        targetBankAccount.setBalance(targetBankAccount.getBalance() + rechargeDepositRequestDTO.getMoney());

        // luu transaction
        Transaction transaction = new Transaction();
        transaction.setId(UILD.nextULID());
        transaction.setIdBankTarget(rechargeDepositRequestDTO.getTargetBankId());
        transaction.setTransactionAmount(rechargeDepositRequestDTO.getMoney());
        transaction.setType(TypeOfTransaction.NAP_TIEN_VAO_TK_GUI_TIEN);
        transaction.setTransactionDate(new Date(System.currentTimeMillis()));

        return transactionRepo.save(transaction);
    }

    @Override
    public Customer addCustomer(String username, Customer customer) {
        if(!(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_ADMIN).isPresent() || accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_EMPLOYEE).isPresent()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);
        if(customerRepo.findCustomerById(customer.getId()).isPresent())
            throw new BadRequestException(ErrorMessage.ID_KHACH_HANG_DA_TON_TAI);
        customer.setAccountId("NO");
        return customerRepo.save(customer);
    }

    @Override
    public List<Customer> getAllCustomer(String username) {
        if(!(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_ADMIN).isPresent() || accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_EMPLOYEE).isPresent()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        return customerRepo.findAll();
    }

    @Override
    public Customer getCustomerById(String username, String customerId) {
        Optional<Customer> customer= customerRepo.findCustomerById(customerId);
        if(!customer.isPresent()) throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_KHACH_HANG);
        return customer.get();
    }

    @Override
    public Customer updateCustomer(String username, Customer customer) {
        Optional<Customer> oldCustomer= customerRepo.findCustomerById(customer.getId());
        if(!oldCustomer.isPresent())
            throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_KHACH_HANG);

        // lấy thông tin khachs hàng của tk đang đăng nhập hiện tại theo accountId
        // sau đó so sánh id customer gửi lên với id của thằng đang đăng nhập,
        // nếu trùng nhau thì chứng tỏ tk đang đăng nhập muốn sửa thông tin của chính nó
        // còn không thì nhân viên hay admin cũng có quyền sửa thông tin tk của khách hàng
        Optional<Account> account = accountRepo.findAccountByUsername(username);
        if(account.get().getRole().equalsIgnoreCase(RoleAccountConstant.ROLE_CUSTOMER) &&
                !customerRepo.findCustomerByAccountId(account.get().getId()).get().getId().equalsIgnoreCase(customer.getId()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);
        oldCustomer.get().setAddress(customer.getAddress());
        oldCustomer.get().setDateOfBirth(customer.getDateOfBirth());
        oldCustomer.get().setFullName(customer.getFullName());
        oldCustomer.get().setIdCard(customer.getIdCard());
        oldCustomer.get().setPhoneNumber(customer.getPhoneNumber());

        return oldCustomer.get();
    }

    @Override
    public Account customerRegistration(AccountRegistrationRequestDTO accountRegistrationRequestDTO) {
        Optional<Customer> customer = customerRepo.findCustomerById(accountRegistrationRequestDTO.getInfoId());
        if(!customer.isPresent())
            throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_KHACH_HANG);
        if(!customer.get().getAccountId().equalsIgnoreCase("NO"))
            throw new BadRequestException(ErrorMessage.KHACH_HANG_DA_CO_TAI_KHOAN);
        if(accountRepo.findAccountByUsername(accountRegistrationRequestDTO.getUsername()).isPresent())
            throw new BadRequestException(ErrorMessage.TEN_TAI_KHOAN_DA_TON_TAI);

        Account account = new Account();
        account.setId(UILD.nextULID());
        account.setUsername(accountRegistrationRequestDTO.getUsername());
        account.setPassword(accountRegistrationRequestDTO.getPassword());
        account.setRole(RoleAccountConstant.ROLE_CUSTOMER);
        account.setEmail(accountRegistrationRequestDTO.getEmail());
        accountRepo.save(account);
        customer.get().setAccountId(account.getId());

        return account;
    }

    @Override
    public Customer deleteCustomer(String username,String customerId) {
        if(!(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_ADMIN).isPresent() || accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_EMPLOYEE).isPresent()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        Optional<Customer> customer = customerRepo.findCustomerById(customerId);
        if(!customer.isPresent())
            throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_KHACH_HANG);

        if(!customer.get().getAccountId().equalsIgnoreCase("NO")){
            Account account = accountRepo.findAccountById(customer.get().getAccountId()).get();
            accountRepo.delete(account);
        }

        Optional<BankAccount> creditAccount = bankAccountRepo.findBankAccountByCustomerIdAndType(customerId,TypeOfBankCard.CREDIT);
        if(creditAccount.isPresent()){
            // xoa cac transaction cua tk nay
            List<Transaction> transactions = transactionRepo.findAllByIdBankSourceOrIdBankTarget(creditAccount.get().getId(),creditAccount.get().getId());
            for(Transaction transaction:transactions) transactionRepo.delete(transaction);
            // xoa tk credits
            creditAccountRepo.delete(creditAccountRepo.findCreditAccountById(creditAccount.get().getId()).get());
            // xoa bank
            bankAccountRepo.delete(creditAccount.get());
        }

        Optional<BankAccount> depositAccount = bankAccountRepo.findBankAccountByCustomerIdAndType(customerId,TypeOfBankCard.DEPOSIT);
        if(depositAccount.isPresent()){
            List<Transaction> transactions = transactionRepo.findAllByIdBankSourceOrIdBankTarget(depositAccount.get().getId(),depositAccount.get().getId());
            for(Transaction transaction:transactions) transactionRepo.delete(transaction);
            // xoa tk deposit
            depositAccountRepo.delete(depositAccountRepo.findDepositAccountById(depositAccount.get().getId()).get());
            // xoa bank
            bankAccountRepo.delete(depositAccount.get());
        }

        customerRepo.delete(customer.get());
        return customer.get();
    }
}
