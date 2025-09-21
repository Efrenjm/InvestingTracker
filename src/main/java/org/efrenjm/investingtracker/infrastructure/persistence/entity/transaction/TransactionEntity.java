package org.efrenjm.investingtracker.infrastructure.persistence.entity.transaction;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.efrenjm.investingtracker.domain.model.transaction.Transaction;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
@Document(collection = "transactions")
public class TransactionEntity extends BaseTransactionEntity
{
	@Field("transaction_date") private Date transactionDate;

	public static TransactionEntity fromDomain(Transaction transaction)
	{
		if (transaction == null)
			return null;

		return populateBaseTransactionEntityFields(TransactionEntity.builder(), transaction)
				.transactionDate(transaction.getTransactionDate())
				.build();
	}

	public Transaction toDomain()
	{
		return populateBaseTransactionDomainFields(Transaction.builder())
				.transactionDate(transactionDate)
				.build();
	}
}
