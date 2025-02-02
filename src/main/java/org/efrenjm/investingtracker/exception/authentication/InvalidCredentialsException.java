package org.efrenjm.investingtracker.exception.authentication;

public class InvalidCredentialsException extends RuntimeException {
	public InvalidCredentialsException() {
		super("Invalid credentials");
	}
}
