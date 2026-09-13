package org.efrenjm.investingtracker.exception.authentication;

public class AccountAlreadyVerifiedException extends RuntimeException {
	public AccountAlreadyVerifiedException() {
		super("Account already verified");
	}
}
