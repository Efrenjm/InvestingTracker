package org.efrenjm.investingtracker.infrastructure.persistence.entity.wallet;

import lombok.*;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.wallet.WalletConfig;
import org.efrenjm.investingtracker.domain.utils.CollectionTransformer;
import org.efrenjm.investingtracker.infrastructure.persistence.utils.MongoUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class WalletConfigEntity
{
	@Field("rules") private Set<ObjectId> ruleEntities;
	@Field("transaction_categories") private Map<String, TransactionSuperCategoryEntity> transactionCategories;

	public static WalletConfigEntity fromDomain(WalletConfig config)
	{
		if (config == null)
			return null;

		return WalletConfigEntity.builder()
				.ruleEntities(MongoUtils.tryParseIds(config.getRules()))
				.transactionCategories(CollectionTransformer.transformMapValues(config.getTransactionCategories(), TransactionSuperCategoryEntity::fromDomain))
				.build();
	}

	public WalletConfig toDomain()
	{
		return WalletConfig.builder()
				.rules(MongoUtils.collectIds(ruleEntities))
				.transactionCategories(CollectionTransformer.transformMapValues(transactionCategories, TransactionSuperCategoryEntity::toDomain))
				.build();
	}
}