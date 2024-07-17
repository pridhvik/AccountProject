package com.account.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = AccountNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException ex) {
		System.out.println("==========Handling Account Not Found Exception==============");
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
	}

	@ExceptionHandler(value = AccountAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleAccountAlreadyExistsException(AccountAlreadyExistsException ex) {
		System.out.println("==========Handling Account Already Exists Exception==============");
		return ResponseEntity.status(HttpStatus.NOT_FOUND.value())
				.body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
	}

	@ExceptionHandler(value = IncorrectPasswordException.class)
	public ResponseEntity<ErrorResponse> handleIncorrectPasswordException(IncorrectPasswordException ex) {
		System.out.println("==========Handling Account Already Exists Exception==============");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
	}
	
	@ExceptionHandler(value = InsufficientFundsException.class)
	public ResponseEntity<ErrorResponse> handleInsufficientFundsException(InsufficientFundsException ex) {
		System.out.println("==========Handling Insufficient Funds Exception==============");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
	}

	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		System.out.println("==========Handling Method Argument Not Valid Exception==============");
		List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage)
				.collect(Collectors.toList());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessages.toString()));
	}

	@ExceptionHandler(value = ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
		System.out.println("==========Handling Constraint Violation Exception==============");
		List<String> violations = ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
				.collect(Collectors.toList());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), violations.toString()));
	}

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<ErrorResponse> handleCommonException(Exception ex) {
		System.out.println("==========Handling Common Exception==============");
		ex.printStackTrace();

		ErrorResponse error = null;

		if (ex instanceof BadCredentialsException) {
			error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "The username or password is incorrect");
		} else if (ex instanceof AccountStatusException) {
			error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "The account is locked");
		} else if (ex instanceof AccessDeniedException) {
			error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You are not authorized to access this resource");
		} else if (ex instanceof SignatureException) {
			error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "The jwt signature is invalid");
		} else if (ex instanceof ExpiredJwtException) {
			error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "The jwt token has expired");
		} else {
			error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
		}

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

}
