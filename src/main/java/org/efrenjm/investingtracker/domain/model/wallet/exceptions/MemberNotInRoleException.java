package org.efrenjm.investingtracker.domain.model.wallet.exceptions;

import org.efrenjm.investingtracker.domain.exception.BadRequestException;

public class MemberNotInRoleException extends BadRequestException
{
	public MemberNotInRoleException(String memberId)
	{
		super("Member '" + memberId + "' doesn't exist in this role.");
	}
}

