package org.efrenjm.investingtracker.application.service.authentication.exceptions;

public class InvalidOldPasswordException extends RuntimeException {
	public InvalidOldPasswordException() {
		super("The old password doesn't match the current password.");
	}
}
