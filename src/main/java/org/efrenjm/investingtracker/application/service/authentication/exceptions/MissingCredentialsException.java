package org.efrenjm.investingtracker.application.service.authentication.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class MissingCredentialsException extends BadRequestException {
	public MissingCredentialsException() {
		super("Email or phone number is required.");
	}
}
