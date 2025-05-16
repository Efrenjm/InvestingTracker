package org.efrenjm.investingtracker.application.service.authentication.exceptions;

import org.springframework.security.authentication.BadCredentialsException;

public class DefaultRegistrationException extends BadCredentialsException {
	public DefaultRegistrationException(String message) {
		super("An unexpected error occurred during registration: " + message);
	}
}
