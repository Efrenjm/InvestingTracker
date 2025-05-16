package org.efrenjm.investingtracker.infrastructure.persistence.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.transaction.Transaction;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Document(collection = "transactions")
public class TransactionEntity {
	@Id @Field("_id") private ObjectId id;
	@Field("name") private String name;
	@Field("description") private String description;
	@Field("type") private String type;
	@Field("from_accounts") private List<AccountAllocationEntity> fromAccounts;
	@Field("to_accounts") private List<AccountAllocationEntity> toAccounts;
	@Field("total_amount") private Double totalAmount;
	@Field("categories") private List<String> categories;
	@Field("transaction_date") private Date transactionDate;
	@Field("created_at") private Date createdAt;
	@Field("updated_at") private Date updatedAt;

	public static TransactionEntity fromDomain(Transaction transaction) {
		return TransactionEntity.builder()
				.id(new ObjectId(transaction.getId()))
				.name(transaction.getName())
				.description(transaction.getDescription())
				.type(transaction.getType())
				.fromAccounts(AccountAllocationEntity.fromDomain(transaction.getFromAccounts().orElse(List.of())))
				.toAccounts(AccountAllocationEntity.fromDomain(transaction.getToAccounts().orElse(List.of())))
				.totalAmount(transaction.getTotalAmount())
				.categories(transaction.getCategories())
				.transactionDate(transaction.getTransactionDate())
				.createdAt(transaction.getCreatedAt())
				.updatedAt(transaction.getUpdatedAt())
				.build();
	}

	public Transaction toDomain() {
		return Transaction.builder()
				.id(id.toString())
				.name(name)
				.description(description)
				.type(type)
				.fromAccounts(AccountAllocationEntity.toDomain(fromAccounts))
				.toAccounts(AccountAllocationEntity.toDomain(toAccounts))
				.totalAmount(totalAmount)
				.categories(categories)
				.transactionDate(transactionDate)
				.createdAt(createdAt)
				.updatedAt(updatedAt)
				.build();
	}
}
