package org.efrenjm.investingtracker.interfaces.rest.exception.client;

public class ForbiddenException extends RuntimeException {
	public ForbiddenException(String message) {
		super(message);
	}
}
