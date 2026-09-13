package org.efrenjm.investingtracker.exception.authentication;

public class UserAlreadyExistsException extends RuntimeException {
	public UserAlreadyExistsException() {
		super("User already exists");
	}
}
