package com.account.exception;

public class IncorrectPasswordException extends RuntimeException {

	public IncorrectPasswordException(String msg) {
		super(msg);
	}
}
