package org.efrenjm.investingtracker.domain.model.user.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class InvalidOldPasswordException extends BadRequestException {
	public InvalidOldPasswordException() {
		super("The old password doesn't match the current password.");
	}
}

