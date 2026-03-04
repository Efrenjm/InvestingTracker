package org.efrenjm.investingtracker.domain.model.account.exceptions;

import org.efrenjm.investingtracker.domain.exception.ConflictException;

public class WalletAlreadySharingAccountException extends ConflictException
{
	public WalletAlreadySharingAccountException(String walletId)
	{
		super("Wallet " + walletId + " already has view access to this account.");
	}
}

