package org.efrenjm.investingtracker.domain.model.wallet.exceptions;

public class CategoryExistsAlready extends RuntimeException
{
	public CategoryExistsAlready(String name)
	{
		super("Subcategory already exists: " + name);
	}
}
