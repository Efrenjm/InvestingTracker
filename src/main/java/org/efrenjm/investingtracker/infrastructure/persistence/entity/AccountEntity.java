package org.efrenjm.investingtracker.infrastructure.persistence.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.account.Account;
import org.efrenjm.investingtracker.domain.model.account.AccountConfig;
import org.efrenjm.investingtracker.domain.model.account.AccountType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Document(collection = "accounts")
public class AccountEntity {
	@Id @Field("_id") private ObjectId id;
	@Field("name") private String name;
	@Field("description") private String description;
	@Field("wallet_id") private ObjectId walletId;
	@Field("sharing_wallets") private List<ObjectId> sharingWallets;
	@Field("type") private EntityAccountType type;
	@Field("available") private Double available;
	@Field("tags") private List<String> tags;
	@Field("created_at") private Date createdAt;
	@Field("updated_at") private Date updatedAt;
	@Field("account_config") private EntityAccountConfig accountConfig;
	@Field("rules") private List<ObjectId> rules;

	/* Debit */
	@Field("goal") private Double goal;

	/* Assets */
	@Field("asset") private String asset;
	@Field("current_price") private Double currentPrice;
	@Field("average_cost") private Double averageCost;

	/* Credit */
	@Field("current_debt") private Double currentDebt;
	@Field("credit_limit") private Double creditLimit;

	public static AccountEntity fromDomain(Account account) {
		return AccountEntity.builder()
				.id(new ObjectId(account.getId()))
				.name(account.getName())
				.description(account.getDescription())
				.walletId(new ObjectId(account.getWalletId()))
				.sharingWallets(account.getSharingWallets().stream().map(ObjectId::new).toList())
				.type(EntityAccountType.valueOf(account.getType().name()))
				.available(account.getAvailable())
				.tags(List.of())
				.createdAt(account.getCreatedAt())
				.updatedAt(account.getUpdatedAt())
				.accountConfig(EntityAccountConfig.fromDomain(account.getAccountConfig()))
				.rules(account.getRules().stream().map(ObjectId::new).toList())
				.goal(account.getGoal())
				.asset(account.getAsset())
				.currentPrice(account.getCurrentPrice())
				.averageCost(account.getAverageCost())
				.currentDebt(account.getCurrentDebt())
				.creditLimit(account.getCreditLimit())
				.build();
	}

	public Account toDomain() {
		return Account.builder()
				.id(id.toString())
				.name(name)
				.description(description)
				.walletId(walletId.toString())
				.sharingWallets(sharingWallets.stream().map(ObjectId::toString).toList())
				.type(type.getAccountType())
				.available(available)
				.tags(tags)
				.createdAt(createdAt)
				.updatedAt(updatedAt)
				.accountConfig(accountConfig.toDomain())
				.rules(rules.stream().map(ObjectId::toString).toList())
				.goal(goal)
				.asset(asset)
				.currentPrice(currentPrice)
				.averageCost(averageCost)
				.currentDebt(currentDebt)
				.creditLimit(creditLimit)
				.build();
	}

	@AllArgsConstructor
	@Builder
	@Getter
	@Setter
	@ToString
	public static class EntityAccountConfig {
		@Field("color") private String color;
		@Field("icon") private String icon;
		@Field("visible") private Boolean visible;
		@Field("image") private String image;
		@Field("included_in_net_sum") private Boolean includedInNetSum;
		@Field("group") private String group;

		public static EntityAccountConfig fromDomain(AccountConfig config) {
			return EntityAccountConfig.builder()
					.color(config.getColor())
					.icon(config.getIcon())
					.visible(config.getVisible())
					.image(config.getImage())
					.includedInNetSum(config.getIncludedInNetSum())
					.group(config.getGroup())
					.build();
		}

		public AccountConfig toDomain() {
			return AccountConfig.builder()
					.color(color)
					.icon(icon)
					.visible(visible)
					.image(image)
					.includedInNetSum(includedInNetSum)
					.group(group)
					.build();
		}
	}

	@RequiredArgsConstructor
	@Getter
	public enum EntityAccountType {
		DEBIT(AccountType.DEBIT),
		CREDIT(AccountType.CREDIT),
		ASSET(AccountType.ASSET);

		private final AccountType accountType;
	}
}

