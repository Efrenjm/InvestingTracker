package org.efrenjm.investingtracker.application.service.authentication.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class ReusedPasswordException extends BadRequestException {
	public ReusedPasswordException() {
		super("The new password can't be the same as the current password.");
	}
}
