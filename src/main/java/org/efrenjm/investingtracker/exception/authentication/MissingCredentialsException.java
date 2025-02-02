package org.efrenjm.investingtracker.exception.authentication;

public class MissingCredentialsException extends RuntimeException {
	public MissingCredentialsException() {
		super("Email or phone number is required");
	}
}
