package com.account.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.account.config.JwtServiceImpl;
import com.account.dto.AccountDto;
import com.account.dto.LoginDto;
import com.account.dto.LoginResponseDto;
import com.account.dto.TransactionDto;
import com.account.entity.Account;
import com.account.service.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("account")
public class AccountController {

	private JwtServiceImpl jwtService;

	private AccountService service;

	public AccountController(JwtServiceImpl jwtService, AccountService service) {
		this.jwtService = jwtService;
		this.service = service;
	}

	@GetMapping("")
	@PreAuthorize("isAuthenticated()")
	public String welcome() {
		return "Welcome to account API";
	}

	@PostMapping("login")
	public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto cred) {
		Account authenticatedUser = service.loginAccount(cred);

		String jwtToken = jwtService.buildToken(authenticatedUser);

		LoginResponseDto loginResp = new LoginResponseDto(jwtToken, jwtService.getExpirationTime());
		return ResponseEntity.status(HttpStatus.OK).body(loginResp);
	}

	@PostMapping("create")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody AccountDto account) {
		System.out.println("Create account in controller");
		return new ResponseEntity<>(service.createAccount(account), HttpStatus.CREATED);
	}

	@GetMapping("/me")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Account> authenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account currentUser = (Account) authentication.getPrincipal();
		return ResponseEntity.ok(currentUser);
	}

	@PutMapping("update")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<AccountDto> updateAccount(@RequestBody AccountDto account) {
		return new ResponseEntity<>(service.updateAccount(account), HttpStatus.OK);
	}

	@GetMapping("getaccount/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<AccountDto> getAccount(@PathVariable(value = "id") Integer accId) {
		return new ResponseEntity<AccountDto>(service.findAccountById(accId), HttpStatus.OK);
	}

	@GetMapping("all")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<AccountDto>> getAllAccounts() {
		return new ResponseEntity<List<AccountDto>>(service.findAllAccounts(), HttpStatus.OK);
	}

	@DeleteMapping("delete/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<String> deleteAccount(@PathVariable(value = "id") Integer accId) {
		return new ResponseEntity<String>(service.deleteAccount(accId), HttpStatus.OK);
	}

	@PutMapping("toggle-activation/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<String> toggleAccountStatus(@PathVariable(value = "id") Integer id) {
		return new ResponseEntity<String>(service.toggleStatus(id), HttpStatus.OK);
	}

	@PutMapping("transfer")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<String> transferFunds(@RequestBody TransactionDto transaction) {
		return new ResponseEntity<String>(service.transferAmount(transaction), HttpStatus.OK);
	}

}
