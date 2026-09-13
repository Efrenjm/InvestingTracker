package org.efrenjm.investingtracker.exception;

public class ConflictException extends RuntimeException {
	public ConflictException(String message) {
		super("Conflict creating resource: " + message);
	}
}
