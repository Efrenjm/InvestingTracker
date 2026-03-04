package org.efrenjm.investingtracker.domain.model.wallet.exceptions;

import org.efrenjm.investingtracker.domain.exception.ConflictException;

public class MemberAlreadyInRoleException extends ConflictException
{
	public MemberAlreadyInRoleException(String memberId)
	{
		super("Member '" + memberId + "' already exists in this role.");
	}
}

