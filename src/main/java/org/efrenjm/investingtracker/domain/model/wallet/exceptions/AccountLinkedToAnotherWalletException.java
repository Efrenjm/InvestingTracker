package org.efrenjm.investingtracker.domain.model.wallet.exceptions;

import org.efrenjm.investingtracker.domain.exception.ConflictException;

public class AccountLinkedToAnotherWalletException extends ConflictException
{
	public AccountLinkedToAnotherWalletException(String accountId)
	{
		super("Account with ID " + accountId + " is already linked to another wallet.");
	}
}
