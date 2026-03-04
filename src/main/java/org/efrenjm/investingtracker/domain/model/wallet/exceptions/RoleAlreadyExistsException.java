package org.efrenjm.investingtracker.domain.model.wallet.exceptions;

import org.efrenjm.investingtracker.domain.exception.ConflictException;

public class RoleAlreadyExistsException extends ConflictException
{
	public RoleAlreadyExistsException(String roleName)
	{
		super("Role '" + roleName + "' already exists in this wallet.");
	}
}

