package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.exception;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class NoUserProvidedException extends BadRequestException {
	public NoUserProvidedException() {
		super("If not authenticated, a User ID must be provided.");
	}
}
