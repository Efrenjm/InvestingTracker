package org.efrenjm.investingtracker.interfaces.web.advice.authentication;

import org.efrenjm.investingtracker.application.service.authentication.exceptions.*;
import org.efrenjm.investingtracker.domain.model.user.exceptions.CodeExpiredException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.InvalidCodeException;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.AuthenticationController;
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

	@ExceptionHandler(DefaultRegistrationException.class)
	public ResponseEntity<String> handleUserRegistration(DefaultRegistrationException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(AccountAlreadyVerifiedException.class)
	public ResponseEntity<String> handleAccountAlreadyVerified(AccountAlreadyVerifiedException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(InvalidCodeException.class)
	public ResponseEntity<String> handleInvalidToken(InvalidCodeException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(CodeExpiredException.class)
	public ResponseEntity<String> handleTokenExpired(CodeExpiredException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
}
