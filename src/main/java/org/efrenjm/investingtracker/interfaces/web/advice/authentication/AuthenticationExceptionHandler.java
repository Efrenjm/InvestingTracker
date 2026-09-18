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
import org.efrenjm.investingtracker.interfaces.web.advice.problem.ApiProblem;
import org.efrenjm.investingtracker.interfaces.web.advice.problem.ApiProblemFactory;

@Slf4j
@RestControllerAdvice(assignableTypes = { AuthenticationController.class})
@Order(1)
public class AuthenticationExceptionHandler {
	private static final String GENERIC_REGISTRATION_MESSAGE =
			"You’re almost there! Check your inbox for the next steps.";

	private final ApiProblemFactory problemFactory;

	public AuthenticationExceptionHandler(ApiProblemFactory problemFactory) {
		this.problemFactory = problemFactory;
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ApiProblem> handleInvalidCredentials(InvalidCredentialsException ex) {
		AppLogger.warn(log, "AUTH-EX-001", "handleInvalidCredentials", ex.getMessage());
		return problemFactory.create(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Authentication failed", ex.getMessage());
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ApiProblem> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
		AppLogger.warn(log, "AUTH-EX-002", "handleUserAlreadyExistsException", ex.getMessage());
		return problemFactory.create(HttpStatus.CONFLICT, "USER_ALREADY_EXISTS", "Registration unavailable", GENERIC_REGISTRATION_MESSAGE);
	}

	@ExceptionHandler(MissingCredentialsException.class)
	public ResponseEntity<ApiProblem> handleMissingCredentials(MissingCredentialsException ex) {
		AppLogger.warn(log, "AUTH-EX-003", "handleMissingCredentials", ex.getMessage());
		return problemFactory.create(HttpStatus.BAD_REQUEST, "MISSING_CREDENTIALS", "Invalid authentication request", ex.getMessage());
	}

	@ExceptionHandler(DefaultRegistrationException.class)
	public ResponseEntity<ApiProblem> handleUserRegistration(DefaultRegistrationException ex) {
		AppLogger.warn(log, "AUTH-EX-004", "handleUserRegistration", ex.getMessage());
		return problemFactory.create(HttpStatus.BAD_REQUEST, "REGISTRATION_FAILED", "Registration failed", ex.getMessage());
	}

	@ExceptionHandler(AccountAlreadyVerifiedException.class)
	public ResponseEntity<ApiProblem> handleAccountAlreadyVerified(AccountAlreadyVerifiedException ex) {
		AppLogger.warn(log, "AUTH-EX-005", "handleAccountAlreadyVerified", ex.getMessage());
		return problemFactory.create(HttpStatus.BAD_REQUEST, "ACCOUNT_ALREADY_VERIFIED", "Account already verified", ex.getMessage());
	}

	@ExceptionHandler(InvalidCodeException.class)
	public ResponseEntity<ApiProblem> handleInvalidToken(InvalidCodeException ex) {
		AppLogger.warn(log, "AUTH-EX-006", "handleInvalidToken", ex.getMessage());
		return problemFactory.create(HttpStatus.BAD_REQUEST, "INVALID_CODE", "Verification failed", ex.getMessage());
	}

	@ExceptionHandler(CodeExpiredException.class)
	public ResponseEntity<ApiProblem> handleTokenExpired(CodeExpiredException ex) {
		AppLogger.warn(log, "AUTH-EX-007", "handleTokenExpired", ex.getMessage());
		return problemFactory.create(HttpStatus.BAD_REQUEST, "CODE_EXPIRED", "Verification failed", ex.getMessage());
	}
}
