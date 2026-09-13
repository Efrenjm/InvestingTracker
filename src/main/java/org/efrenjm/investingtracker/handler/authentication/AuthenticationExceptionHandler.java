package org.efrenjm.investingtracker.handler.authentication;

import org.efrenjm.investingtracker.controller.authentication.AuthenticationController;
import org.efrenjm.investingtracker.exception.authentication.*;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = { AuthenticationController.class})
@Order(1)
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
	public ResponseEntity<String> handleUserRegistration(UserRegistrationException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(AccountAlreadyVerifiedException.class)
	public ResponseEntity<String> handleAccountAlreadyVerified(AccountAlreadyVerifiedException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<String> handleInvalidToken(InvalidTokenException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(TokenExpiredException.class)
	public ResponseEntity<String> handleTokenExpired(TokenExpiredException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
}
