package org.efrenjm.investingtracker.domain.model.user.exceptions;

public class WalletNotLinkedToUserException extends RuntimeException
{
	public WalletNotLinkedToUserException(String userId, String walletId)
	{
		super("Wallet with id " + walletId + " is not linked to user with id " + userId);
	}
}
