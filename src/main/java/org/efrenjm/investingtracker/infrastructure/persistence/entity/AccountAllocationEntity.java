package org.efrenjm.investingtracker.infrastructure.persistence.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.transaction.AccountAllocation;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class AccountAllocationEntity {
	@Field("account") private ObjectId account;
	@Field("amount") private double amount;

	public static AccountAllocationEntity fromDomain(AccountAllocation allocation) {
		return AccountAllocationEntity.builder()
				.account(new ObjectId(allocation.getAccount()))
				.amount(allocation.getAmount())
				.build();
	}

	public AccountAllocation toDomain() {
		return AccountAllocation.builder()
				.account(account.toString())
				.amount(amount)
				.build();
	}

	public static List<AccountAllocationEntity> fromDomain(List<AccountAllocation> allocations) {
		return allocations.stream()
				.map(AccountAllocationEntity::fromDomain)
				.toList();
	}

	public static List<AccountAllocation> toDomain(List<AccountAllocationEntity> allocations) {
		return allocations.stream()
				.map(AccountAllocationEntity::toDomain)
				.toList();
	}
}

