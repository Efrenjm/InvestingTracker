package org.efrenjm.investingtracker.domain.model.user.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class NoVerificationInProcessException extends BadRequestException {
	public NoVerificationInProcessException() {
		super("No verification in process.");
	}
}
