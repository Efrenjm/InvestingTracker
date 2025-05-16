package org.efrenjm.investingtracker.application.service.authentication.exceptions;

import org.efrenjm.investingtracker.domain.exception.ConflictException;

public class RegistrationInProgressException extends ConflictException {
	public RegistrationInProgressException() {
		super("Registration in progress. A new code has been sent to you.");
	}
}
