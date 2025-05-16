package org.efrenjm.investingtracker.application.service.user_service.exceptions;

import org.efrenjm.investingtracker.domain.exception.ResourceNotFoundException;

public class WalletNotFoundException extends ResourceNotFoundException {
	public WalletNotFoundException(String walletId) {
		super(String.format("Wallet with id  %s not found.", walletId));
	}
}
