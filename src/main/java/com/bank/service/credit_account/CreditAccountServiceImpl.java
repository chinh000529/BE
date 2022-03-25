package com.bank.service.credit_account;

import com.bank.constant.ErrorMessage;
import com.bank.constant.RoleAccountConstant;
import com.bank.constant.TypeOfBankCard;
import com.bank.dto.request.CreditBankRequestDTO;
import com.bank.entity.BankAccount;
import com.bank.entity.CreditAccount;
import com.bank.entity.Customer;
import com.bank.exception.BadRequestException;
import com.bank.repository.AccountRepo;
import com.bank.repository.BankAccountRepo;
import com.bank.repository.CreditAccountRepo;
import com.bank.repository.CustomerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreditAccountServiceImpl implements CreditAccountService {

    private final CreditAccountRepo creditAccountRepo;
    private final AccountRepo accountRepo;
    private final BankAccountRepo bankAccountRepo;
    private final CustomerRepo customerRepo;

    @Override
    public CreditAccount save(CreditBankRequestDTO creditBankRequestDTO) {
        return creditAccountRepo.save(
                new CreditAccount(creditBankRequestDTO.getId(),creditBankRequestDTO.getMaxLoan(),creditBankRequestDTO.getExpirationDate()));
    }

    @Override
    public List<CreditBankRequestDTO> getAllCreditBankAccounts(String username) {
        if(!(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_ADMIN).isPresent() || accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_EMPLOYEE).isPresent()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        List<BankAccount> creditBanks = bankAccountRepo.findAllByType(TypeOfBankCard.CREDIT);
        List<CreditBankRequestDTO> creditBanksResponse = new ArrayList<>();

        for(BankAccount bankAccount:creditBanks){
            CreditAccount creditAccount = creditAccountRepo.getById(bankAccount.getId());

            creditBanksResponse.add(toCreditBankResponse(bankAccount,creditAccount));
        }
        return creditBanksResponse;
    }

    @Override
    public CreditBankRequestDTO getCreditBankById(String username, String id) {
        if(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_CUSTOMER).isPresent()){
            Customer customer = customerRepo.findCustomerByAccountId(accountRepo.findAccountByUsername(username).get().getId()).get();

            // tim trong bang bankaccount
            Optional<BankAccount> bankAccount = bankAccountRepo.findBankAccountByCustomerIdAndType(customer.getId(), TypeOfBankCard.CREDIT);
            if(!bankAccount.isPresent() || !bankAccount.get().getId().equalsIgnoreCase(id))
                throw new BadRequestException(ErrorMessage.KHONG_THE_XEM_THONG_TIN_TAI_KHOAN_CUA_NGUOI_KHAC);

            CreditAccount creditAccount = creditAccountRepo.findCreditAccountById(bankAccount.get().getId()).get();
            return toCreditBankResponse(bankAccount.get(),creditAccount);
        }else{
            Optional<BankAccount> bankAccount = bankAccountRepo.findBankAccountByIdAndType(id, TypeOfBankCard.CREDIT);
            if(!bankAccount.isPresent())
                throw new BadRequestException(ErrorMessage.TAI_KHOAN_TIN_DUNG_KHONG_TON_TAI);

            CreditAccount creditAccount = creditAccountRepo.findCreditAccountById(bankAccount.get().getId()).get();
            return toCreditBankResponse(bankAccount.get(),creditAccount);
        }
    }

    @Override
    public CreditBankRequestDTO getCreditBankByIdCustomer(String username, String idCustomer) {
        if(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_CUSTOMER).isPresent()){
            Customer customer = customerRepo.findCustomerByAccountId(accountRepo.findAccountByUsername(username).get().getId()).get();
            if(!customer.getId().equalsIgnoreCase(idCustomer))
                throw new BadRequestException(ErrorMessage.KHONG_THE_XEM_THONG_TIN_TAI_KHOAN_CUA_NGUOI_KHAC);
        }
        Optional<BankAccount> bankAccount = bankAccountRepo.findBankAccountByCustomerIdAndType(idCustomer,TypeOfBankCard.CREDIT);
        if(!bankAccount.isPresent())
            throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_TAI_KHOAN_TIN_DUNG_THEO_ID_KHACH_HANG);

        CreditAccount creditAccount = creditAccountRepo.findCreditAccountById(bankAccount.get().getId()).get();
        return toCreditBankResponse(bankAccount.get(),creditAccount);
    }

    @Override
    public CreditBankRequestDTO updateCreditBankAccount(String username, CreditBankRequestDTO creditBankRequestDTO) {
        if(!(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_ADMIN).isPresent() || accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_EMPLOYEE).isPresent()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        BankAccount bankAccount = bankAccountRepo.getById(creditBankRequestDTO.getId());
        CreditAccount creditAccount = creditAccountRepo.findCreditAccountById(creditBankRequestDTO.getId()).get();

        bankAccount.setBalance(creditBankRequestDTO.getBalance());
        bankAccount.setInterestRate(creditBankRequestDTO.getInterestRate());
        bankAccount.setCreatedDate(creditBankRequestDTO.getCreatedDate());
        bankAccount.setCustomerId(creditBankRequestDTO.getCustomerId());
        bankAccount.setEmployeeId(creditBankRequestDTO.getEmployeeId());

        creditAccount.setExpirationDate(creditBankRequestDTO.getExpirationDate());
        creditAccount.setMaxLoan(creditBankRequestDTO.getMaxLoan());

        return creditBankRequestDTO;
    }

    public CreditBankRequestDTO toCreditBankResponse(BankAccount bankAccount, CreditAccount creditAccount){
        CreditBankRequestDTO creditBankResponse = new CreditBankRequestDTO();

        creditBankResponse.setId(bankAccount.getId());
        creditBankResponse.setBalance(bankAccount.getBalance());
        creditBankResponse.setInterestRate(bankAccount.getInterestRate());
        creditBankResponse.setCreatedDate(bankAccount.getCreatedDate());
        creditBankResponse.setMaxLoan(creditAccount.getMaxLoan());
        creditBankResponse.setExpirationDate(creditAccount.getExpirationDate());
        creditBankResponse.setCustomerId(bankAccount.getCustomerId());
        creditBankResponse.setEmployeeId(bankAccount.getEmployeeId());
        return creditBankResponse;
    }
}
