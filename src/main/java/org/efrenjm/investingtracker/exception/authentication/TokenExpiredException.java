package org.efrenjm.investingtracker.exception.authentication;

public class TokenExpiredException extends RuntimeException {
	public TokenExpiredException() {
		super("Token expired");
	}
}
