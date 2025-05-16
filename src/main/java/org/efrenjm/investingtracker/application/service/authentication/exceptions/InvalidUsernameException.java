package org.efrenjm.investingtracker.application.service.authentication.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class InvalidUsernameException extends BadRequestException {
	public InvalidUsernameException(String username) {
		super("Invalid username: " + username + ". Username must be a valid email or phone number.");
	}
}
