package org.efrenjm.investingtracker.domain.model.wallet.exceptions;

public class CategoryNotFound extends RuntimeException
{
	public CategoryNotFound(String name)
	{
		super("Subcategory doesn't exist: " + name);
	}
}
