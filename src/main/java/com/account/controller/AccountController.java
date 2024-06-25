package com.account.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.client.RestTemplate;

import com.account.config.JwtService;
import com.account.dto.AccountDto;
import com.account.dto.LoginDto;
import com.account.dto.LoginResponseDto;
import com.account.entity.Account;
import com.account.service.AccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("account")
public class AccountController {

	private JwtService jwtService;

	private AccountService service;

	public AccountController(JwtService jwtService, AccountService service) {
		this.jwtService = jwtService;
		this.service = service;
	}

	@GetMapping("")
	public String welcome() {
		return "Welcome to account API";
	}

	@PostMapping("login")
	public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto cred) {
		Account authenticatedUser = service.loginAccount(cred);

		String jwtToken = jwtService.generateToken(authenticatedUser);

		LoginResponseDto loginResp = new LoginResponseDto(jwtToken, jwtService.getExpirationTime());
		return ResponseEntity.status(HttpStatus.OK).body(loginResp);
	}

	@PostMapping("create")
	public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody AccountDto account) {
		System.out.println("Create account in controller");
		return new ResponseEntity<>(service.createAccount(account), HttpStatus.CREATED);
	}

	@GetMapping("/me")
	public ResponseEntity<Account> authenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account currentUser = (Account) authentication.getPrincipal();
		return ResponseEntity.ok(currentUser);
	}

	@PutMapping("update")
	public ResponseEntity<AccountDto> updateAccount(@RequestBody AccountDto account) {
		return new ResponseEntity<>(service.updateAccount(account), HttpStatus.OK);
	}

	@GetMapping("getaccount/{id}")
	public ResponseEntity<AccountDto> getAccount(@PathVariable(value = "id") Integer accId) {
		return new ResponseEntity<AccountDto>(service.findAccountById(accId), HttpStatus.OK);
	}

	@GetMapping("all")
	public ResponseEntity<List<AccountDto>> getAllAccounts() {
		return new ResponseEntity<List<AccountDto>>(service.findAllAccounts(), HttpStatus.OK);
	}

	@DeleteMapping("delete/{id}")
	public ResponseEntity<String> deleteAccount(@PathVariable(value = "id") Integer accId) {
		return new ResponseEntity<String>(service.deleteAccount(accId), HttpStatus.OK);
	}

	@PutMapping("toggle-activation/{id}")
	public ResponseEntity<String> toggleAccountStatus(@PathVariable(value = "id") Integer id) {
		return new ResponseEntity<String>(service.toggleStatus(id), HttpStatus.OK);
	}

	@GetMapping("external-user")
	public void externalUsers() throws JsonMappingException, JsonProcessingException {
		RestTemplate rt = new RestTemplate();

		List<Object> jsonResp = rt.getForObject("https://jsonplaceholder.typicode.com/users", List.class);
		System.out.println(jsonResp);
		for (Object obj : jsonResp) {
			Map<String, Object> user = (Map) obj;
			if (user.get("id") == (Object) 2 || user.get("id") == (Object) 6) {

				Map<String, Object> add = (Map) user.get("address");
				Map<String, Object> geo = (Map) add.get("geo");
				System.out.println(geo.get("lat"));
			}
		}
	}

}
