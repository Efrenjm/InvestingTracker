package org.efrenjm.investingtracker.domain.model.wallet;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.efrenjm.investingtracker.domain.model.wallet.exceptions.CategoryExistsAlready;
import org.efrenjm.investingtracker.domain.model.wallet.exceptions.CategoryNotFound;

import java.util.*;

@SuperBuilder
@Getter
@Setter
@ToString
public class TransactionSuperCategory extends BaseTransactionCategory
{
	@Builder.Default
	Map<String, TransactionCategory> subcategories = new HashMap<>();

	public void addCategory(String name, TransactionCategory config)
	{
		ensureSubcategoriesInitialized();
		if (subcategories.containsKey(name))
			throw new CategoryExistsAlready(name);
		subcategories.put(name, config);
	}

	public void removeCategory(String name)
	{
		ensureSubcategoriesInitialized();
		if (!subcategories.containsKey(name))
			throw new CategoryNotFound(name);
		subcategories.remove(name);
	}

	public void updateCategory(String name, TransactionCategory subCategory)
	{
		ensureSubcategoriesInitialized();
		if (!subcategories.containsKey(name))
			throw new CategoryNotFound(name);
		subcategories.replace(name, subCategory);
	}

	private void ensureSubcategoriesInitialized()
	{
		if (subcategories == null)
			subcategories = new HashMap<>();
	}
}
