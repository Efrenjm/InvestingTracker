package org.efrenjm.investingtracker.application.service.authentication.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class InvalidCredentialsException extends BadRequestException {
	public InvalidCredentialsException() {
		super("Invalid user/password combination. Please try again.");
	}
}
