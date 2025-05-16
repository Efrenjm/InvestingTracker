package org.efrenjm.investingtracker.infrastructure.persistence.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.transaction.Periodicity;
import org.efrenjm.investingtracker.domain.model.transaction.Rule;
import org.efrenjm.investingtracker.domain.model.wallet.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Document(collection = "wallets")
public class WalletEntity {
	@Id @Field("_id") private ObjectId id;
	@Field("name") private String name;
	@Field("description") private String description;
	@Field("roles") private List<List<EntityRole>> roles;
	@Field("accounts") private List<ObjectId> accounts;
	@Field("created_by") private ObjectId createdBy;
	@Field("created_at") private Date createdAt;
	@Field("updated_at") private Date updatedAt;
	@Field("configuration") private EntityWalletConfig configuration;

	public static WalletEntity fromDomain(Wallet wallet) {
		return WalletEntity.builder()
				.id(new ObjectId(wallet.getId()))
				.name(wallet.getName())
				.description(wallet.getDescription())
				.roles(wallet.getRoles().stream().map(rolesStage -> rolesStage.stream().map(EntityRole::fromDomain).toList()).toList())
				.accounts(wallet.getAccounts().stream().map(ObjectId::new).toList())
				.createdBy(new ObjectId(wallet.getCreatedBy()))
				.createdAt(wallet.getCreatedAt())
				.updatedAt(wallet.getUpdatedAt())
				.configuration(EntityWalletConfig.fromDomain(wallet.getConfiguration()))
				.build();
	}

	public Wallet toDomain() {
		return Wallet.builder()
				.id(id.toHexString())
				.name(name)
				.description(description)
				.roles(roles.stream().map(rolesStage -> rolesStage.stream().map(EntityRole::toDomain).toList()).toList())
				.accounts(accounts.stream().map(ObjectId::toHexString).toList())
				.createdBy(createdBy.toHexString())
				.createdAt(createdAt)
				.updatedAt(updatedAt)
				.configuration(configuration.toDomain())
				.build();
	}

	@AllArgsConstructor
	@Builder
	@Getter
	@Setter
	@ToString
	public static class EntityWalletConfig {
		@Field("rules") private List<RuleEntity> ruleEntities;
		@Field("transaction_categories") private List<EntityTransactionCategory> transactionCategories;

		public static EntityWalletConfig fromDomain(WalletConfig config) {
			return EntityWalletConfig.builder()
					.ruleEntities(config.getRules().stream().map(RuleEntity::fromDomain).toList())
					.transactionCategories(config.getTransactionCategories().stream().map(EntityTransactionCategory::fromDomain).toList())
					.build();
		}

		public WalletConfig toDomain() {
			return WalletConfig.builder()
					.rules(ruleEntities.stream().map(RuleEntity::toDomain).toList())
					.transactionCategories(transactionCategories.stream().map(EntityTransactionCategory::toDomain).toList())
					.build();
		}
	}

	@AllArgsConstructor
	@Builder
	@Getter
	@Setter
	@ToString
	public static class EntityRole {
		@Field("name") private String name;
		@Field("description") private String description;
		@Field("permissions") private EntityPermissions entityPermissions;
		@Field("members") private List<ObjectId> members;

		public static EntityRole fromDomain(Role role) {
			return EntityRole.builder()
					.name(role.getName())
					.description(role.getDescription())
					.entityPermissions(EntityPermissions.fromDomain(role.getPermissions()))
					.members(role.getMembers().stream().map(ObjectId::new).toList())
					.build();
		}

		public Role toDomain() {
			return Role.builder()
					.name(name)
					.description(description)
					.permissions(entityPermissions.toDomain())
					.members(members.stream().map(ObjectId::toHexString).toList())
					.build();
		}

		@AllArgsConstructor
		@Builder
		@Getter
		@Setter
		@ToString
		public static class EntityPermissions {
			@Field("accounts") private EntityCRUDPermissions accounts;
			@Field("transactions") private EntityCRUDPermissions transactions;
			@Field("rules") private EntityCRUDPermissions rules;
			@Field("members") private EntityCRUDPermissions members;
			@Field("wallet") private EntityRUDPermissions wallet;

			public static EntityPermissions fromDomain(Role.Permissions permissions) {
				return EntityPermissions.builder()
						.accounts(EntityCRUDPermissions.fromDomain(permissions.getAccounts()))
						.transactions(EntityCRUDPermissions.fromDomain(permissions.getTransactions()))
						.rules(EntityCRUDPermissions.fromDomain(permissions.getRules()))
						.members(EntityCRUDPermissions.fromDomain(permissions.getMembers()))
						.wallet(EntityRUDPermissions.fromDomain(permissions.getWallet()))
						.build();
			}

			public Role.Permissions toDomain() {
				return Role.Permissions.builder()
						.accounts(accounts.toDomain())
						.transactions(transactions.toDomain())
						.rules(rules.toDomain())
						.members(members.toDomain())
						.wallet(wallet.toDomain())
						.build();
			}

			@AllArgsConstructor
			@Builder
			@Getter
			@Setter
			@ToString
			public static class EntityRUDPermissions {
				@Field("view") private boolean view;
				@Field("edit") private boolean edit;
				@Field("remove") private boolean remove;

				public static EntityRUDPermissions fromDomain(Role.Permissions.RUDPermissions permissions) {
					return EntityRUDPermissions.builder()
							.view(permissions.isView())
							.edit(permissions.isEdit())
							.remove(permissions.isRemove())
							.build();
				}

				public Role.Permissions.RUDPermissions toDomain() {
					return Role.Permissions.RUDPermissions.builder()
							.view(view)
							.edit(edit)
							.remove(remove)
							.build();
				}
			}

			@Getter
			@Setter
			@ToString
			public static class EntityCRUDPermissions extends EntityRUDPermissions {
				@Field("add") private boolean add;

				EntityCRUDPermissions(boolean view, boolean edit, boolean remove, boolean add) {
					super(view, edit, remove);
					this.add = add;
				}

				public static EntityCRUDPermissions fromDomain(Role.Permissions.CRUDPermissions permissions) {
					return new EntityCRUDPermissions(
							permissions.isView(),
							permissions.isEdit(),
							permissions.isRemove(),
							permissions.isAdd()
					);
				}

				@Override
				public Role.Permissions.CRUDPermissions toDomain() {
					return Role.Permissions.CRUDPermissions.builder()
							.add(this.isAdd())
							.view(this.isView())
							.edit(this.isEdit())
							.remove(this.isRemove())
							.build();
				}
			}
		}
	}

	@AllArgsConstructor
	@Builder
	@Getter
	@Setter
	@ToString
	public static class RuleEntity {
		@Id @Field("_id") private String id;
		@Field("name") private String name;
		@Field("description") private String description;
		@Field("type") private String type;
		@Field("from_accounts") private List<AccountAllocationEntity> fromAccounts;
		@Field("to_accounts") private List<AccountAllocationEntity> toAccounts;
		@Field("total_amount") private Double totalAmount;
		@Field("categories") private List<String> categories;
		@Field("automatic") private Boolean automatic;
		@Field("initial_date") private Date initialDate;
		@Field("periodicity") private PeriodicityEntity periodicity;
		@Field("created_at") private Date createdAt;
		@Field("updated_at") private Date updatedAt;
		@Field("tags") private List<String> tags;


		public static RuleEntity fromDomain(Rule rule) {
			return RuleEntity.builder()
					.id(rule.getId())
					.name(rule.getName())
					.description(rule.getDescription())
					.type(rule.getType())
					.fromAccounts(AccountAllocationEntity.fromDomain(rule.getFromAccounts().orElse(List.of())))
					.toAccounts(AccountAllocationEntity.fromDomain(rule.getToAccounts().orElse(List.of())))
					.totalAmount(rule.getTotalAmount())
					.categories(rule.getCategories())
					.automatic(rule.getAutomatic())
					.initialDate(rule.getInitialDate())
					.periodicity(PeriodicityEntity.fromDomain(rule.getPeriodicity()))
					.createdAt(rule.getCreatedAt())
					.updatedAt(rule.getUpdatedAt())
					.tags(rule.getTags())
					.build();
		}

		public Rule toDomain() {
			return Rule.builder()
					.id(id)
					.name(name)
					.description(description)
					.type(type)
					.automatic(automatic)
					.fromAccounts(AccountAllocationEntity.toDomain(fromAccounts))
					.toAccounts(AccountAllocationEntity.toDomain(toAccounts))
					.totalAmount(totalAmount)
					.categories(categories)
					.initialDate(initialDate)
					.periodicity(periodicity != null ? periodicity.toDomain() : null)
					.createdAt(createdAt)
					.updatedAt(updatedAt)
					.tags(tags)
					.build();
		}

		@AllArgsConstructor
		@Builder
		@Getter
		@Setter
		@ToString
		public static class PeriodicityEntity {
			@Field("amount") private int amount;
			@Field("period") private String period;

			public static PeriodicityEntity fromDomain(Periodicity periodicity) {
				if (periodicity == null) {
					return null;
				}
				return PeriodicityEntity.builder()
						.amount(periodicity.getAmount())
						.period(periodicity.getPeriod())
						.build();
			}

			public Periodicity toDomain() {
				return Periodicity.builder()
						.amount(amount)
						.period(period)
						.build();
			}
		}
	}

	@AllArgsConstructor
	@Builder
	@Getter
	@Setter
	@ToString
	public static class EntityTransactionCategory {
		@Field("name") private String name;
		@Field("color") private String color;
		@Field("icon") private String icon;
		@Field("type") private String type;

		public static EntityTransactionCategory fromDomain(TransactionCategory category) {
			return EntityTransactionCategory.builder()
					.name(category.getName())
					.color(category.getColor())
					.icon(category.getIcon())
					.type(category.getType())
					.build();
		}

		public TransactionCategory toDomain() {
			return TransactionCategory.builder()
					.name(name)
					.color(color)
					.icon(icon)
					.type(type)
					.build();
		}
	}
}
