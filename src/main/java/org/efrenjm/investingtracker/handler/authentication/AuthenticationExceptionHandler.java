package org.efrenjm.investingtracker.handler.authentication;

import org.efrenjm.investingtracker.exception.authentication.InvalidCredentialsException;
import org.efrenjm.investingtracker.exception.authentication.MissingCredentialsException;
import org.efrenjm.investingtracker.exception.authentication.UserAlreadyExistsException;
import org.efrenjm.investingtracker.exception.authentication.UserRegistrationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class AuthenticationExceptionHandler {
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<String> handleInvalidCredentials(InvalidCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}

	@ExceptionHandler(MissingCredentialsException.class)
	public ResponseEntity<String> handleMissingCredentials(MissingCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(UserRegistrationException.class)
	public ResponseEntity<String> handleUserCreation(UserRegistrationException ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
	}
}
