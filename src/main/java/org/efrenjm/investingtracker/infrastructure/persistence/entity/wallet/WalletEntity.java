package org.efrenjm.investingtracker.infrastructure.persistence.entity.wallet;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.wallet.*;
import org.efrenjm.investingtracker.domain.utils.CollectionTransformer;
import org.efrenjm.investingtracker.infrastructure.persistence.entity.AuditableMongoEntity;
import org.efrenjm.investingtracker.infrastructure.persistence.utils.MongoUtils;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
@Document(collection = "wallets")
public class WalletEntity extends AuditableMongoEntity
{
	@Field("name") private String name;
	@Field("description") private String description;
	@Field("roles") private Map<String, RoleEntity> roles;
	@Field("accounts") private Set<ObjectId> accounts;
	@Field("configuration") private WalletConfigEntity configuration;

	public static WalletEntity fromDomain(Wallet wallet)
	{
		if (wallet == null)
			return null;

		return populateAuditableEntityFields(WalletEntity.builder(), wallet)
				.name(wallet.getName())
				.description(wallet.getDescription())
				.roles(CollectionTransformer.transformMapValues(wallet.getRoles().orElse(Map.of()), RoleEntity::fromDomain))
				.accounts(MongoUtils.tryParseIds(wallet.getAccounts().orElse(Set.of())))
				.configuration(WalletConfigEntity.fromDomain(wallet.getConfiguration().orElse(null)))
				.build();
	}

	public Wallet toDomain()
	{
		return populateAuditableDomainFields(Wallet.builder())
				.name(name)
				.description(description)
				.roles(CollectionTransformer.transformMapValues(roles, RoleEntity::toDomain))
				.accounts(MongoUtils.collectIds(accounts))
				.configuration(Optional.ofNullable(configuration)
						.map(WalletConfigEntity::toDomain)
						.orElse(null))
				.build();
	}
}
