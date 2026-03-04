package org.efrenjm.investingtracker.domain.model.user.exceptions;

import org.efrenjm.investingtracker.domain.exception.ConflictException;

public class WalletAlreadyLinkedToUserException extends ConflictException
{
	public WalletAlreadyLinkedToUserException(String walletId, String userId)
	{
		super("Wallet with id " + walletId + " is already linked to user with id " + userId);
	}
}
