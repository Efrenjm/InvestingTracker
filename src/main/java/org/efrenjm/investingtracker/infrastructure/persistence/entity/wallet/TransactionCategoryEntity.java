package org.efrenjm.investingtracker.infrastructure.persistence.entity.wallet;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.efrenjm.investingtracker.domain.model.wallet.TransactionCategory;

@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public class TransactionCategoryEntity extends BaseTransactionCategoryEntity
{
	public static TransactionCategoryEntity fromDomain(TransactionCategory category)
	{
		if (category == null)
			return null;

		return populateBaseEntityFields(TransactionCategoryEntity.builder(), category)
				.build();
	}

	public TransactionCategory toDomain()
	{
		return populateBaseDomainFields(TransactionCategory.builder())
				.build();
	}
}