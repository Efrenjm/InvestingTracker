package org.efrenjm.investingtracker.application.service.authentication.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class InvalidCodeUsageException extends BadRequestException {
	public InvalidCodeUsageException() {
		super("Invalid code usage. Please verify the request and try again.");
	}
}
