package org.efrenjm.investingtracker.domain.exception;

public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(String message) {
		super("Could not found resource: " + message);
	}
}
