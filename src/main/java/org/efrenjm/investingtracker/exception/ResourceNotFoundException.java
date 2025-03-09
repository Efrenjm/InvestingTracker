package org.efrenjm.investingtracker.exception;

public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(String message) {
		super("Could not found resource: " + message);
	}
}
