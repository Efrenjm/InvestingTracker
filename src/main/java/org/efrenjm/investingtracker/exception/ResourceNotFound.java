package org.efrenjm.investingtracker.exception;

public class ResourceNotFound extends RuntimeException {
	public ResourceNotFound(String message) {
		super("Could not found resource: " + message);
	}
}
