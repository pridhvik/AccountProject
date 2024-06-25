package com.account.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.account.exception.AccountNotFoundException;
import com.account.repository.AccountRepository;
import com.account.utility.ErrorMessages;

@Configuration
public class ApplicationConfiguration {

	private final AccountRepository repo;

	public ApplicationConfiguration(AccountRepository repo) {
		this.repo = repo;
	}

	@Bean
	UserDetailsService userDetailsService() {
		return userName -> repo.findByEmail(userName)
				.orElseThrow(() -> new AccountNotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND));
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

}
