package org.efrenjm.investingtracker.infrastructure.persistence.entity.account;

import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.account.AccountConfig;
import org.efrenjm.investingtracker.domain.model.account.AccountType;
import org.efrenjm.investingtracker.domain.model.account.AssetAccount;
import org.efrenjm.investingtracker.domain.model.account.BaseAccount;
import org.efrenjm.investingtracker.domain.model.account.CreditAccount;
import org.efrenjm.investingtracker.domain.model.account.DebitAccount;
import org.efrenjm.investingtracker.infrastructure.persistence.entity.AuditableMongoEntity;
import org.efrenjm.investingtracker.infrastructure.persistence.entity.account.exception.NonSupportedAccountTypeException;
import org.efrenjm.investingtracker.infrastructure.persistence.utils.MongoUtils;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString
@Document(collection = "accounts")
public class AccountEntity extends AuditableMongoEntity {
    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("wallet_id")
    private ObjectId walletId;

    @Field("sharing_wallets")
    private Set<ObjectId> sharingWallets;

    @Field("type")
    private AccountType type;

    @Field("available")
    private Double available;

    @Field("tags")
    private Set<String> tags;

    @Field("account_config")
    private EntityAccountConfig accountConfig;

    @Field("rules")
    private Set<ObjectId> rules;

    /* Debit */
    @Field("goal")
    private Double goal;

    /* Assets */
    @Field("asset")
    private String asset;

    @Field("current_price")
    private Double currentPrice;

    @Field("average_cost")
    private Double averageCost;

    /* Credit */
    @Field("current_debt")
    private Double currentDebt;

    @Field("credit_limit")
    private Double creditLimit;

    public static AccountEntity fromDomain(BaseAccount account) {
        if (account == null) {
            return null;
        }

        return switch (account.getType()) {
            case DEBIT -> fromDomain((DebitAccount) account);
            case CREDIT -> fromDomain((CreditAccount) account);
            case ASSET -> fromDomain((AssetAccount) account);
            default -> throw new NonSupportedAccountTypeException(account.getType().getType());
        };
    }

    public static AccountEntity fromDomain(DebitAccount account) {
        if (account == null) {
            return null;
        }

        return buildBase(account).goal(account.getGoal()).build();
    }

    public static AccountEntity fromDomain(CreditAccount account) {
        if (account == null) {
            return null;
        }

        return buildBase(account)
                .currentDebt(account.getCurrentDebt())
                .creditLimit(account.getCreditLimit())
                .build();
    }

    public static AccountEntity fromDomain(AssetAccount account) {
        if (account == null) {
            return null;
        }

        return buildBase(account)
                .asset(account.getAsset())
                .currentPrice(account.getCurrentPrice())
                .averageCost(account.getAverageCost())
                .goal(account.getGoal())
                .build();
    }

    public BaseAccount toDomain() {
        BaseAccount.BaseAccountBuilder<?, ?> builder =
                switch (type) {
                    case DEBIT -> DebitAccount.builder().goal(goal);
                    case CREDIT ->
                            CreditAccount.builder()
                                    .currentDebt(currentDebt)
                                    .creditLimit(creditLimit);
                    case ASSET ->
                            AssetAccount.builder()
                                    .asset(asset)
                                    .currentPrice(currentPrice)
                                    .averageCost(averageCost)
                                    .goal(goal);
                    default -> throw new NonSupportedAccountTypeException(type.getType());
                };

        builder.name(name)
                .description(description)
                .walletId(walletId.toHexString())
                .sharingWallets(MongoUtils.collectIds(sharingWallets))
                .type(type)
                .available(available)
                .tags(tags)
                .accountConfig(
                        Optional.ofNullable(accountConfig)
                                .map(EntityAccountConfig::toDomain)
                                .orElse(null))
                .rules(MongoUtils.collectIds(rules));
        return populateAuditableDomainFields(builder).build();
    }

    private static AccountEntity.AccountEntityBuilder<?, ?> buildBase(BaseAccount account) {
        AccountEntity.AccountEntityBuilder<?, ?> builder =
                AccountEntity.builder()
                        .name(account.getName())
                        .description(account.getDescription())
                        .walletId(MongoUtils.idToEntity(account.getWalletId()))
                        .sharingWallets(
                                MongoUtils.tryParseIds(
                                        account.getSharingWallets().orElse(Set.of())))
                        .type(account.getType())
                        .available(account.getAvailable())
                        .tags(account.getTags().orElse(Set.of()))
                        .accountConfig(EntityAccountConfig.fromDomain(account.getAccountConfig()))
                        .rules(MongoUtils.tryParseIds(account.getRules().orElse(Set.of())));

        return populateAuditableEntityFields(builder, account);
    }

    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    @ToString
    public static class EntityAccountConfig {
        @Field("color")
        private String color;

        @Field("icon")
        private String icon;

        @Field("visible")
        private Boolean visible;

        @Field("image")
        private String image;

        @Field("included_in_net_sum")
        private Boolean includedInNetSum;

        @Field("group")
        private String group;

        public static EntityAccountConfig fromDomain(AccountConfig config) {
            if (config == null) {
                return null;
            }

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
}
