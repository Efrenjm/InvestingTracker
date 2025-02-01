package org.efrenjm.investingtracker.exception.authentication;

public class InvalidCredentialsException extends RuntimeException {
	public InvalidCredentialsException(String message) {
		super(message);
	}
}
