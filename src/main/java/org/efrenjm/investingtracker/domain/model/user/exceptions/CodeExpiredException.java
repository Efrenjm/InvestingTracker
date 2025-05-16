package org.efrenjm.investingtracker.domain.model.user.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class CodeExpiredException extends BadRequestException {
	public CodeExpiredException() {
		super("Code expired. A new code has been sent to you.");
	}
}
