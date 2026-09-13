package org.efrenjm.investingtracker.exception.authentication;

import org.efrenjm.investingtracker.exception.ConflictException;

public class RegistrationInProgressException extends ConflictException {
	public RegistrationInProgressException() {
		super("Registration in progress. A new code was sent to the provided resource.");
	}
}
