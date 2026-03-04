package org.efrenjm.investingtracker.domain.model.wallet.exceptions;

import org.efrenjm.investingtracker.domain.exception.ConflictException;

public class CategoryExistsAlready extends ConflictException
{
	public CategoryExistsAlready(String name)
	{
		super("Subcategory already exists: " + name);
	}
}
