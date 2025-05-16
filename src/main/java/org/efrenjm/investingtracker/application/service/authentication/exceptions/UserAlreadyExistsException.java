package org.efrenjm.investingtracker.application.service.authentication.exceptions;

import org.efrenjm.investingtracker.domain.exception.ConflictException;

public class UserAlreadyExistsException extends ConflictException {
	public UserAlreadyExistsException() {

		// This message should be the same as the one of the succesful registration
		super("We've send you a verification code to your email. Please check your inbox.");
	}
}
