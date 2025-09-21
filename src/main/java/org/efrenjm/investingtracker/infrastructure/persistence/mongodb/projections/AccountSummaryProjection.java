package org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections;

import lombok.*;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.dto.AccountSummary;
import org.efrenjm.investingtracker.domain.model.account.AccountType;
import org.efrenjm.investingtracker.infrastructure.persistence.entity.account.AccountEntity;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class AccountSummaryProjection {
	@Field("_id") private ObjectId id;
	@Field("name") private String name;
	@Field("description") private String description;
	@Field("type") private AccountType type;
	@Field("available") private Double available;
	@Field("tags") private Set<String> tags;
	@Field("account_config") private AccountEntity.EntityAccountConfig accountConfig;

	public static AccountSummaryProjection fromDomain(AccountSummary accountSummary) {
		return AccountSummaryProjection.builder()
				.id(new ObjectId(accountSummary.getId()))
				.name(accountSummary.getName())
				.description(accountSummary.getDescription())
				.type(accountSummary.getType())
				.available(accountSummary.getAvailable())
				.tags(accountSummary.getTags())
				.accountConfig(AccountEntity.EntityAccountConfig.fromDomain(accountSummary.getAccountConfig()))
				.build();
	}

	public AccountSummary toDomain() {
		return AccountSummary.builder()
				.id(id.toString())
				.name(name)
				.description(description)
				.type(type)
				.available(available)
				.tags(tags != null ? tags : new HashSet<>())
				.accountConfig(accountConfig.toDomain())
				.build();
	}
}
