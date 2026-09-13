package org.efrenjm.investingtracker.exception.authentication;

public class InvalidCodeException extends RuntimeException {
	public InvalidCodeException() { super("Invalid token"); }
}
