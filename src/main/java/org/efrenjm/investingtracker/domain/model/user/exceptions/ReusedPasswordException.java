package org.efrenjm.investingtracker.domain.model.user.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class ReusedPasswordException extends BadRequestException {
	public ReusedPasswordException() {
		super("The new password can't be the same as the current password.");
	}
}

