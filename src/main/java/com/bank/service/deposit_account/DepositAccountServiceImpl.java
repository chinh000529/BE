package com.bank.service.deposit_account;

import com.bank.constant.ErrorMessage;
import com.bank.constant.RoleAccountConstant;
import com.bank.constant.TypeOfBankCard;
import com.bank.dto.request.DepositBankRequestDTO;
import com.bank.entity.BankAccount;
import com.bank.entity.Customer;
import com.bank.entity.DepositAccount;
import com.bank.exception.BadRequestException;
import com.bank.repository.AccountRepo;
import com.bank.repository.BankAccountRepo;
import com.bank.repository.CustomerRepo;
import com.bank.repository.DepositAccountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DepositAccountServiceImpl implements DepositAccountService {
    private final DepositAccountRepo depositAccountRepo;
    private final AccountRepo accountRepo;
    private final BankAccountRepo bankAccountRepo;
    private final CustomerRepo customerRepo;

    @Override
    public DepositAccount save(DepositBankRequestDTO depositBankRequestDTO) {
        return depositAccountRepo.save(
                new DepositAccount(depositBankRequestDTO.getId(),depositBankRequestDTO.getMinBalance(),depositBankRequestDTO.getFirstRecharge(),depositBankRequestDTO.getFirstDepositDate()));
    }

    @Override
    public List<DepositBankRequestDTO> getAllDepositBankAccounts(String username) {
        if(!(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_ADMIN).isPresent() || accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_EMPLOYEE).isPresent()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        List<BankAccount> depositBanks = bankAccountRepo.findAllByType(TypeOfBankCard.DEPOSIT);
        List<DepositBankRequestDTO> depositBankResponse = new ArrayList<>();

        for(BankAccount bankAccount:depositBanks){
            DepositAccount depositAccount = depositAccountRepo.getById(bankAccount.getId());
            depositBankResponse.add(toDepositBankResponse(bankAccount,depositAccount));
        }

        return depositBankResponse;
    }

    @Override
    public DepositBankRequestDTO getDepositBankById(String username, String id) {
        if(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_CUSTOMER).isPresent()){
            Customer customer = customerRepo.findCustomerByAccountId(accountRepo.findAccountByUsername(username).get().getId()).get();

            // tim trong bang bankaccount
            Optional<BankAccount> bankAccount = bankAccountRepo.findBankAccountByCustomerIdAndType(customer.getId(), TypeOfBankCard.DEPOSIT);
            if(!bankAccount.isPresent() || !bankAccount.get().getId().equalsIgnoreCase(id))
                throw new BadRequestException(ErrorMessage.KHONG_THE_XEM_THONG_TIN_TAI_KHOAN_CUA_NGUOI_KHAC);

            DepositAccount depositAccount = depositAccountRepo.findDepositAccountById(bankAccount.get().getId()).get();
            return toDepositBankResponse(bankAccount.get(),depositAccount);
        }else{
            Optional<BankAccount> bankAccount = bankAccountRepo.findBankAccountByIdAndType(id, TypeOfBankCard.DEPOSIT);
            if(!bankAccount.isPresent())
                throw new BadRequestException(ErrorMessage.TAI_KHOAN_GUI_TIEN_KHONG_TON_TAI);

            DepositAccount depositAccount = depositAccountRepo.findDepositAccountById(bankAccount.get().getId()).get();
            return toDepositBankResponse(bankAccount.get(),depositAccount);
        }
    }

    @Override
    public DepositBankRequestDTO getDepositBankByIdCustomer(String username, String idCustomer) {
        if(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_CUSTOMER).isPresent()){
            Customer customer = customerRepo.findCustomerByAccountId(accountRepo.findAccountByUsername(username).get().getId()).get();
            if(!customer.getId().equalsIgnoreCase(idCustomer))
                throw new BadRequestException(ErrorMessage.KHONG_THE_XEM_THONG_TIN_TAI_KHOAN_CUA_NGUOI_KHAC);

            // tim trong bang bankaccount
            Optional<BankAccount> bankAccount = bankAccountRepo.findBankAccountByCustomerIdAndType(customer.getId(), TypeOfBankCard.DEPOSIT);
            if(!bankAccount.isPresent()){
                throw new BadRequestException(ErrorMessage.KHACH_HANG_CHUA_CO_TAI_KHOAN_GUI_TIEN);
            }
            DepositAccount depositAccount = depositAccountRepo.findDepositAccountById(bankAccount.get().getId()).get();
            return toDepositBankResponse(bankAccount.get(),depositAccount);
        }else{
            Optional<BankAccount> bankAccount = bankAccountRepo.findBankAccountByCustomerIdAndType(idCustomer,TypeOfBankCard.DEPOSIT);
            if(!bankAccount.isPresent())
                throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_ACCOUNT_THEO_CUSTOMER_ID);

            DepositAccount depositAccount = depositAccountRepo.findDepositAccountById(bankAccount.get().getId()).get();
            return toDepositBankResponse(bankAccount.get(),depositAccount);
        }
    }

    @Override
    public DepositBankRequestDTO updateDepositBankAccount(String username, DepositBankRequestDTO depositBankRequestDTO) {
        if(!(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_ADMIN).isPresent() || accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_EMPLOYEE).isPresent()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        BankAccount bankAccount = bankAccountRepo.getById(depositBankRequestDTO.getId());
        DepositAccount depositAccount = depositAccountRepo.findDepositAccountById(depositBankRequestDTO.getId()).get();

        bankAccount.setBalance(depositBankRequestDTO.getBalance());
        bankAccount.setInterestRate(depositBankRequestDTO.getInterestRate());
        bankAccount.setCreatedDate(depositBankRequestDTO.getCreatedDate());
        bankAccount.setCustomerId(depositBankRequestDTO.getCustomerId());
        bankAccount.setEmployeeId(depositBankRequestDTO.getEmployeeId());

        depositAccount.setMinBalance(depositBankRequestDTO.getMinBalance());
        depositAccount.setFirstDepositDate(depositBankRequestDTO.getFirstDepositDate());
        depositAccount.setFirstRecharge(depositBankRequestDTO.getFirstRecharge());

        return depositBankRequestDTO;
    }

    public DepositBankRequestDTO toDepositBankResponse(BankAccount bankAccount, DepositAccount depositAccount){
        DepositBankRequestDTO depositBankResponse = new DepositBankRequestDTO();
        depositBankResponse.setId(bankAccount.getId());
        depositBankResponse.setBalance(bankAccount.getBalance());
        depositBankResponse.setInterestRate(bankAccount.getInterestRate());
        depositBankResponse.setCreatedDate(bankAccount.getCreatedDate());
        depositBankResponse.setMinBalance(depositAccount.getMinBalance());
        depositBankResponse.setFirstRecharge(depositAccount.getFirstRecharge());
        depositBankResponse.setFirstDepositDate(depositAccount.getFirstDepositDate());
        depositBankResponse.setCustomerId(bankAccount.getCustomerId());
        depositBankResponse.setEmployeeId(bankAccount.getEmployeeId());

        return depositBankResponse;
    }
}
