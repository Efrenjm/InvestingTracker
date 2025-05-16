package org.efrenjm.investingtracker.domain.model.user.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class InvalidCodeException extends BadRequestException {
	public InvalidCodeException() {
		super("Invalid code. Please try again.");
	}
}
