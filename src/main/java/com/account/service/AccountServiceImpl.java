package com.account.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.BeanUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.account.dto.AccountDto;
import com.account.dto.LoginDto;
import com.account.dto.TransactionDto;
import com.account.entity.Account;
import com.account.exception.AccountAlreadyExistsException;
import com.account.exception.AccountNotFoundException;
import com.account.exception.InsufficientFundsException;
import com.account.repository.AccountRepository;
import com.account.utility.ErrorMessages;

@Service
public class AccountServiceImpl implements AccountService {

	private AccountRepository repo;

	private PasswordEncoder passwordEncoder;

	private AuthenticationManager authenticationManager;

	private KafkaTemplate<String, Object> template;

	public AccountServiceImpl(AccountRepository repo, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, KafkaTemplate<String, Object> template) {
		this.repo = repo;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.template = template;
	}

	@Override
	public Account loginAccount(LoginDto cred) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(cred.getUserName(), cred.getPassword()));

		return repo.findByEmail(cred.getUserName())
				.orElseThrow(() -> new AccountNotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND + cred.getUserName()));
//		Optional<Account> byEmail = repo.findByEmail(cred.getUserName());
//		if (byEmail.isEmpty()) {
//			throw new AccountNotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND + cred.getUserName());
//		} else {
//			Account account = byEmail.get();
//			if (!passwordEncoder.matches(cred.getPassword(), account.getPassword())) {
//				throw new IncorrectPasswordException(ErrorMessages.INCORRECT_PASSWORD);
//			}
//			return account.viewAsAccountDto();
//		}
	}

	@Override
	public AccountDto createAccount(AccountDto account) {
		if (repo.findByEmail(account.getEmail()).isPresent()) {
			throw new AccountAlreadyExistsException(ErrorMessages.ACCOUNT_ALREADY_EXISTS + account.getEmail());
		}
		Account acc = new Account();
		BeanUtils.copyProperties(account, acc);
		acc.setPassword(passwordEncoder.encode(account.getPassword()));
		acc.setStatus(false);
		acc.setAccountNumber(UUID.randomUUID().toString());
		acc.setAccountType("SAVINGS");
		acc.setBankName("HDFC");
		acc.setIfscCode("HDFCB0001007");
		acc.setBalance(10000.0);
		System.out.println("Hashed password " + acc.getPassword());
		return repo.save(acc).viewAsAccountDto();
	}

	@Override
	public AccountDto updateAccount(AccountDto account) {
		return repo.findById(account.getAccId()).map(acc -> {
			BeanUtils.copyProperties(account, acc);
			return repo.save(acc);
		}).orElseThrow(() -> new AccountNotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND + account.getAccId()))
				.viewAsAccountDto();
	}

	@Override
	public AccountDto findAccountById(Integer id) {
		return repo.findById(id).orElseThrow(() -> new AccountNotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND + id))
				.viewAsAccountDto();
	}

	@Override
	public List<AccountDto> findAllAccounts() {
		return repo.findAll().stream().map(Account::viewAsAccountDto).toList();
	}

	@Override
	public String deleteAccount(Integer id) {
		if (repo.findById(id).isEmpty()) {
			throw new AccountNotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND + id);
		}
		repo.deleteById(id);
		return "Account deleted successfully";
	}

	@Override
	public String toggleStatus(Integer id) {
		return repo.findById(id).map(acc -> {
			acc.setStatus(!acc.getStatus());
			return repo.save(acc).getStatus() ? "Account activated successfully" : "Account deactivated successfully";
		}).orElseThrow(() -> new AccountNotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND + id));
	}

	@Override
	public String transferAmount(TransactionDto transaction) {
		return repo.findByAccountNumber(transaction.getAccountNumber()).map(acc -> {
			if (acc.getBalance() < transaction.getTransactionAmount()) {
				throw new InsufficientFundsException(ErrorMessages.INSUFFICIENT_FUNDS);
			}
			acc.setBalance(acc.getBalance() - transaction.getTransactionAmount());
			repo.save(acc);

			transaction.setTransactionId(UUID.randomUUID().toString());
			transaction.setUserName(acc.getUsername());
			sendTransactionEvent(transaction);
			return "Transaction successful";
		}).orElseThrow(
				() -> new AccountNotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND + transaction.getAccountNumber()));
	}

	public void sendTransactionEvent(TransactionDto transaction) {
		try {
			CompletableFuture<SendResult<String, Object>> future = template.send("account-transactions", transaction);
			future.whenComplete((result, ex) -> {
				if (ex == null) {
					System.out.println("Sent message=[" + transaction.toString() + "] with offset=["
							+ result.getRecordMetadata().offset() + "]");
				} else {
					System.out.println(
							"Unable to send message=[" + transaction.toString() + "] due to : " + ex.getMessage());
				}
			});
		} catch (Exception ex) {
			System.out.println("Error: " + ex.getMessage());
		}
	}
}
