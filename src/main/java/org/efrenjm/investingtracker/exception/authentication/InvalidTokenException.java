package org.efrenjm.investingtracker.exception.authentication;

public class InvalidTokenException extends RuntimeException {
	public InvalidTokenException() { super("Invalid token"); }
}
