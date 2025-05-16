package org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections;

import lombok.*;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.dto.AccountSummary;
import org.efrenjm.investingtracker.infrastructure.persistence.entity.AccountEntity;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class AccountSummaryProjection {
	@Field("_id") private ObjectId id;
	@Field("name") private String name;
	@Field("description") private String description;
	@Field("type") private AccountEntity.EntityAccountType type;
	@Field("available") private Double available;
	@Field("tags") private List<String> tags;
	@Field("account_config") private AccountEntity.EntityAccountConfig accountConfig;

	public static AccountSummaryProjection fromDomain(AccountSummary accountSummary) {
		return AccountSummaryProjection.builder()
				.id(new ObjectId(accountSummary.getId()))
				.name(accountSummary.getName())
				.description(accountSummary.getDescription())
				.type(AccountEntity.EntityAccountType.valueOf(accountSummary.getType().name()))
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
				.type(type.getAccountType())
				.available(available)
				.tags(tags)
				.accountConfig(accountConfig.toDomain())
				.build();
	}
}
