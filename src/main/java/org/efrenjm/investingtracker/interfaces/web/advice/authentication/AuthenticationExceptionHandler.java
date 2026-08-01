package org.efrenjm.investingtracker.interfaces.web.advice.authentication;

import lombok.extern.slf4j.Slf4j;
import org.efrenjm.investingtracker.application.service.authentication.exceptions.*;
import org.efrenjm.investingtracker.domain.model.user.exceptions.CodeExpiredException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.InvalidCodeException;
import org.efrenjm.investingtracker.infrastructure.logging.AppLogger;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.AuthenticationController;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = { AuthenticationController.class})
@Order(1)
public class AuthenticationExceptionHandler {
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<String> handleInvalidCredentials(InvalidCredentialsException ex) {
		AppLogger.warn(log, "AUTH-EX-001", "handleInvalidCredentials", ex.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
		AppLogger.warn(log, "AUTH-EX-002", "handleUserAlreadyExistsException", ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}

	@ExceptionHandler(MissingCredentialsException.class)
	public ResponseEntity<String> handleMissingCredentials(MissingCredentialsException ex) {
		AppLogger.warn(log, "AUTH-EX-003", "handleMissingCredentials", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(DefaultRegistrationException.class)
	public ResponseEntity<String> handleUserRegistration(DefaultRegistrationException ex) {
		AppLogger.warn(log, "AUTH-EX-004", "handleUserRegistration", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(AccountAlreadyVerifiedException.class)
	public ResponseEntity<String> handleAccountAlreadyVerified(AccountAlreadyVerifiedException ex) {
		AppLogger.warn(log, "AUTH-EX-005", "handleAccountAlreadyVerified", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(InvalidCodeException.class)
	public ResponseEntity<String> handleInvalidToken(InvalidCodeException ex) {
		AppLogger.warn(log, "AUTH-EX-006", "handleInvalidToken", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(CodeExpiredException.class)
	public ResponseEntity<String> handleTokenExpired(CodeExpiredException ex) {
		AppLogger.warn(log, "AUTH-EX-007", "handleTokenExpired", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
}
