package org.efrenjm.investingtracker.domain.model.wallet.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class AccountNotLinkedToWallet extends BadRequestException
{
	public AccountNotLinkedToWallet(String accountId, String walletId)
	{
		super("Account with ID " + accountId + " doesn't belong to wallet " + walletId + ".");
	}
}
