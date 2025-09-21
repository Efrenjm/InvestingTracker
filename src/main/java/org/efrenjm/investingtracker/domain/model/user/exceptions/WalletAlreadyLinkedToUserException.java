package org.efrenjm.investingtracker.domain.model.user.exceptions;

public class WalletAlreadyLinkedToUserException extends RuntimeException
{
	public WalletAlreadyLinkedToUserException(String walletId, String userId)
	{
		super("Wallet with id " + walletId + " is already linked to user with id " + userId);
	}
}
