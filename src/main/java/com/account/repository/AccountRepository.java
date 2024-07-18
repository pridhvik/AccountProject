package com.account.repository;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.account.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Serializable> {

	public Optional<Account> findByEmail(String email);

	public Optional<Account> findByAccountNumber(String accountNumber);
}
