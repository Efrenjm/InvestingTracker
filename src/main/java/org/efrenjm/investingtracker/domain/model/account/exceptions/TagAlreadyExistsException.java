package org.efrenjm.investingtracker.domain.model.account.exceptions;

import org.efrenjm.investingtracker.domain.exception.ConflictException;

public class TagAlreadyExistsException extends ConflictException
{
	public TagAlreadyExistsException(String tag)
	{
		super("Tag '" + tag + "' already exists in the account.");
	}
}

