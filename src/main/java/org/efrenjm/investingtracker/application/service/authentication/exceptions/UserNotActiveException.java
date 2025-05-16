package org.efrenjm.investingtracker.application.service.authentication.exceptions;

public class UserNotActiveException extends RuntimeException {
	public UserNotActiveException() {
		super("This account is disabled. More instructions have been sent to you.");
	}
}
