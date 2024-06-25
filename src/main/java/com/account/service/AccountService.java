package com.account.service;

import java.util.List;

import com.account.dto.AccountDto;
import com.account.dto.LoginDto;
import com.account.dto.TransactionDto;
import com.account.entity.Account;

public interface AccountService {

	public AccountDto createAccount(AccountDto account);

	public AccountDto updateAccount(AccountDto account);

	public AccountDto findAccountById(Integer id);

	public List<AccountDto> findAllAccounts();

	public String deleteAccount(Integer id);

	public String toggleStatus(Integer id);

	public Account loginAccount(LoginDto cred);
	
	public String transferAmount(TransactionDto transaction);
}
