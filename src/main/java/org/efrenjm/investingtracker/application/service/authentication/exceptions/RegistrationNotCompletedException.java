package org.efrenjm.investingtracker.application.service.authentication.exceptions;

public class RegistrationNotCompletedException extends RuntimeException {
	public RegistrationNotCompletedException() {
		super("Registration not completed. A new code has been sent to you.");
	}
}
