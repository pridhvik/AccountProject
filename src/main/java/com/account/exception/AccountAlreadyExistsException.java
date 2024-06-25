package com.account.exception;

public class AccountAlreadyExistsException extends RuntimeException {
	public AccountAlreadyExistsException(String msg) {
		super(msg);
	}
}
