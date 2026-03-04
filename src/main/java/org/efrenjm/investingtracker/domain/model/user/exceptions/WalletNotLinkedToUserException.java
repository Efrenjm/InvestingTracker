package org.efrenjm.investingtracker.domain.model.user.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class WalletNotLinkedToUserException extends BadRequestException
{
	public WalletNotLinkedToUserException(String userId, String walletId)
	{
		super("Wallet with id " + walletId + " is not linked to user with id " + userId);
	}
}
