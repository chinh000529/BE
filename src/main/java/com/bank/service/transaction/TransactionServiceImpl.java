package com.bank.service.transaction;

import com.bank.constant.ErrorMessage;
import com.bank.constant.RoleAccountConstant;
import com.bank.constant.TypeOfBankCard;
import com.bank.dto.request.TransactionRequest;
import com.bank.dto.response.TransactionResponseDTO;
import com.bank.entity.BankAccount;
import com.bank.entity.Customer;
import com.bank.entity.Transaction;
import com.bank.exception.BadRequestException;
import com.bank.repository.AccountRepo;
import com.bank.repository.BankAccountRepo;
import com.bank.repository.CustomerRepo;
import com.bank.repository.TransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepo transactionRepo;
    private final AccountRepo accountRepo;
    private final BankAccountRepo bankAccountRepo;
    private final CustomerRepo customerRepo;

    @Override
    public List<TransactionResponseDTO> getCreditTransactionsByTime(String username, TransactionRequest transactionRequest) {
        if(!(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_ADMIN).isPresent() || accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_EMPLOYEE).isPresent()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        List<Transaction> transactions = transactionRepo.findAllByTransactionDateLessThanEqualAndTransactionDateGreaterThanEqual(
                transactionRequest.getEndDate(),transactionRequest.getStartDate());

        if(transactions.isEmpty())
            throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_GIAO_DICH_NAO);

        List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();
        for(Transaction transaction:transactions){
            Optional<BankAccount> sourceBankAccount = bankAccountRepo.findBankAccountById(transaction.getIdBankSource());
            if(sourceBankAccount.isPresent() && sourceBankAccount.get().getType().equalsIgnoreCase(TypeOfBankCard.CREDIT)){
                transactionResponseDTOS.add(toTransactionResponseDTO(transaction,sourceBankAccount.get()));
            }
            Optional<BankAccount> targetBankAccount = bankAccountRepo.findBankAccountById(transaction.getIdBankTarget());
            if(targetBankAccount.isPresent() && targetBankAccount.get().getType().equalsIgnoreCase(TypeOfBankCard.CREDIT)){
                transactionResponseDTOS.add(toTransactionResponseDTO(transaction,targetBankAccount.get()));
            }
        }
        if(transactionResponseDTOS.isEmpty()) throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_GIAO_DICH_NAO);
        return transactionResponseDTOS;
    }

    @Override
    public List<TransactionResponseDTO> getCreditTransactionByID(String username, String id) {
        Optional<Transaction> transaction = transactionRepo.findById(id);
        if(!transaction.isPresent()) throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_GIAO_DICH_NAO);

        List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();

        Optional<BankAccount> sourceBankAccount = bankAccountRepo.findBankAccountById(transaction.get().getIdBankSource());
        if(sourceBankAccount.isPresent() && sourceBankAccount.get().getType().equalsIgnoreCase(TypeOfBankCard.CREDIT)){
            transactionResponseDTOS.add(toTransactionResponseDTO(transaction.get(),sourceBankAccount.get()));
        }
        Optional<BankAccount> targetBankAccount = bankAccountRepo.findBankAccountById(transaction.get().getIdBankTarget());
        if(targetBankAccount.isPresent() && targetBankAccount.get().getType().equalsIgnoreCase(TypeOfBankCard.CREDIT)){
            transactionResponseDTOS.add(toTransactionResponseDTO(transaction.get(),targetBankAccount.get()));
        }

        return transactionResponseDTOS;
    }

    @Override
    public List<TransactionResponseDTO> getCreditTransactionsByIDCustomer(String username, String idCustomer) {
        // từ mã khách hàng -> mã tài khoản tín dụng (nếu không có tài khoản tín dụng thì thông báo ko có giao dịch nào luôn)
        // -> đưa về hàm tìm kiếm giao dịch theo mã tài khoản nguồn hoặc đích
        Optional<Customer> customer = customerRepo.findCustomerById(idCustomer);
        if(!customer.isPresent()) throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_KHACH_HANG);

        Optional<BankAccount> bankAccount = bankAccountRepo.findBankAccountByCustomerIdAndType(idCustomer, TypeOfBankCard.CREDIT);
        if(!bankAccount.isPresent()) throw new BadRequestException(ErrorMessage.KHACH_HANG_CHUA_CO_TAI_KHOAN_TIN_DUNG);

        List<Transaction> transactions = transactionRepo.findAllByIdBankSourceOrIdBankTarget(bankAccount.get().getId(),bankAccount.get().getId());
        List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();

        for(Transaction transaction:transactions){
            transactionResponseDTOS.add(toTransactionResponseDTO(transaction,bankAccount.get()));
        }

        return transactionResponseDTOS;
    }

    @Override
    public List<TransactionResponseDTO> getDepositTransactionsByTime(String username, TransactionRequest transactionRequest) {
        if(!(accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_ADMIN).isPresent() || accountRepo.findAccountByUsernameAndRole(username, RoleAccountConstant.ROLE_EMPLOYEE).isPresent()))
            throw new BadRequestException(ErrorMessage.KHONG_CO_QUYEN);

        List<Transaction> transactions = transactionRepo.findAllByTransactionDateLessThanEqualAndTransactionDateGreaterThanEqual(
                transactionRequest.getEndDate(),transactionRequest.getStartDate());

        if(transactions.isEmpty())
            throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_GIAO_DICH_NAO);

        List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();
        for(Transaction transaction:transactions){
            Optional<BankAccount> sourceBankAccount = bankAccountRepo.findBankAccountById(transaction.getIdBankSource());
            if(sourceBankAccount.isPresent() && sourceBankAccount.get().getType().equalsIgnoreCase(TypeOfBankCard.DEPOSIT)){
                transactionResponseDTOS.add(toTransactionResponseDTO(transaction,sourceBankAccount.get()));
            }
            Optional<BankAccount> targetBankAccount = bankAccountRepo.findBankAccountById(transaction.getIdBankTarget());
            if(targetBankAccount.isPresent() && targetBankAccount.get().getType().equalsIgnoreCase(TypeOfBankCard.DEPOSIT)){
                transactionResponseDTOS.add(toTransactionResponseDTO(transaction,targetBankAccount.get()));
            }
        }
        if(transactionResponseDTOS.isEmpty()) throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_GIAO_DICH_NAO);
        return transactionResponseDTOS;
    }

    @Override
    public List<TransactionResponseDTO> getDepositTransactionByID(String username, String id) {
        Optional<Transaction> transaction = transactionRepo.findById(id);
        if(!transaction.isPresent()) throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_GIAO_DICH_NAO);

        List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();

        Optional<BankAccount> sourceBankAccount = bankAccountRepo.findBankAccountById(transaction.get().getIdBankSource());
        if(sourceBankAccount.isPresent() && sourceBankAccount.get().getType().equalsIgnoreCase(TypeOfBankCard.DEPOSIT)){
            transactionResponseDTOS.add(toTransactionResponseDTO(transaction.get(),sourceBankAccount.get()));
        }
        Optional<BankAccount> targetBankAccount = bankAccountRepo.findBankAccountById(transaction.get().getIdBankTarget());
        if(targetBankAccount.isPresent() && targetBankAccount.get().getType().equalsIgnoreCase(TypeOfBankCard.DEPOSIT)){
            transactionResponseDTOS.add(toTransactionResponseDTO(transaction.get(),targetBankAccount.get()));
        }

        return transactionResponseDTOS;
    }

    @Override
    public List<TransactionResponseDTO> getDepositTransactionsByIDCustomer(String username, String idCustomer) {
        Optional<Customer> customer = customerRepo.findCustomerById(idCustomer);
        if(!customer.isPresent()) throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_KHACH_HANG);

        Optional<BankAccount> bankAccount = bankAccountRepo.findBankAccountByCustomerIdAndType(idCustomer, TypeOfBankCard.DEPOSIT);
        if(!bankAccount.isPresent()) throw new BadRequestException(ErrorMessage.KHACH_HANG_CHUA_CO_TAI_KHOAN_GUI_TIEN);

        List<Transaction> transactions = transactionRepo.findAllByIdBankSourceOrIdBankTarget(bankAccount.get().getId(),bankAccount.get().getId());
        List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();

        for(Transaction transaction:transactions){
            transactionResponseDTOS.add(toTransactionResponseDTO(transaction,bankAccount.get()));
        }

        return transactionResponseDTOS;
    }

    @Override
    public List<TransactionResponseDTO> getCreditTransactionByIDCustomerAndTime(String username, String idCustomer, TransactionRequest transactionRequest) {
        Optional<Customer> customer = customerRepo.findCustomerById(idCustomer);
        if(!customer.isPresent()) throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_KHACH_HANG);

        Optional<BankAccount> bankAccount = bankAccountRepo.findBankAccountByCustomerIdAndType(idCustomer, TypeOfBankCard.CREDIT);
        if(!bankAccount.isPresent()) throw new BadRequestException(ErrorMessage.KHACH_HANG_CHUA_CO_TAI_KHOAN_TIN_DUNG);

        List<Transaction> transaction1 =
                transactionRepo.findAllByIdBankSourceAndTransactionDateLessThanEqualAndTransactionDateGreaterThanEqual(bankAccount.get().getId(),transactionRequest.getEndDate(),transactionRequest.getStartDate());
        List<Transaction> transaction2 =
                transactionRepo.findAllByIdBankTargetAndTransactionDateLessThanEqualAndTransactionDateGreaterThanEqual(bankAccount.get().getId(),transactionRequest.getEndDate(),transactionRequest.getStartDate());
        List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();

        for(Transaction transaction:transaction1){
            transactionResponseDTOS.add(toTransactionResponseDTO(transaction,bankAccount.get()));
        }
        for(Transaction transaction:transaction2){
            transactionResponseDTOS.add(toTransactionResponseDTO(transaction,bankAccount.get()));
        }

        return transactionResponseDTOS;
    }

    @Override
    public List<TransactionResponseDTO> getDepositTransactionByIDCustomerAndTime(String username, String idCustomer, TransactionRequest transactionRequest) {
        Optional<Customer> customer = customerRepo.findCustomerById(idCustomer);
        if(!customer.isPresent()) throw new BadRequestException(ErrorMessage.KHONG_TON_TAI_KHACH_HANG);

        Optional<BankAccount> bankAccount = bankAccountRepo.findBankAccountByCustomerIdAndType(idCustomer, TypeOfBankCard.DEPOSIT);
        if(!bankAccount.isPresent()) throw new BadRequestException(ErrorMessage.KHACH_HANG_CHUA_CO_TAI_KHOAN_GUI_TIEN);

        List<Transaction> transaction1 =
                transactionRepo.findAllByIdBankSourceAndTransactionDateLessThanEqualAndTransactionDateGreaterThanEqual(bankAccount.get().getId(),transactionRequest.getEndDate(),transactionRequest.getStartDate());
        List<Transaction> transaction2 =
                transactionRepo.findAllByIdBankTargetAndTransactionDateLessThanEqualAndTransactionDateGreaterThanEqual(bankAccount.get().getId(),transactionRequest.getEndDate(),transactionRequest.getStartDate());
        List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();

        for(Transaction transaction:transaction1){
            transactionResponseDTOS.add(toTransactionResponseDTO(transaction,bankAccount.get()));
        }
        for(Transaction transaction:transaction2){
            transactionResponseDTOS.add(toTransactionResponseDTO(transaction,bankAccount.get()));
        }

        return transactionResponseDTOS;
    }

    public TransactionResponseDTO toTransactionResponseDTO(Transaction transaction, BankAccount bankAccount){
        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();
        transactionResponseDTO.setId(transaction.getId());
        transactionResponseDTO.setCustomerId(bankAccount.getCustomerId());
        transactionResponseDTO.setCustomerName(customerRepo.findCustomerById(bankAccount.getCustomerId()).get().getFullName());
        transactionResponseDTO.setAmount(transaction.getTransactionAmount());
        transactionResponseDTO.setType(transaction.getType());
        transactionResponseDTO.setIdBankSource(transaction.getIdBankSource());
        transactionResponseDTO.setIdBankTarget(transaction.getIdBankTarget());
        transactionResponseDTO.setTransactionDate(transaction.getTransactionDate());
        transactionResponseDTO.setContent(transaction.getContent());

        return transactionResponseDTO;
    }
}
