package org.efrenjm.investingtracker.infrastructure.persistence.entity.transaction;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.transaction.BaseTransaction;
import org.efrenjm.investingtracker.domain.model.transaction.TransactionType;
import org.efrenjm.investingtracker.domain.utils.CollectionTransformer;
import org.efrenjm.investingtracker.infrastructure.persistence.entity.AuditableMongoEntity;
import org.efrenjm.investingtracker.infrastructure.persistence.utils.MongoUtils;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public abstract class BaseTransactionEntity extends AuditableMongoEntity {
    @Field("name")
    protected String name;

    @Field("description")
    protected String description;

    @Field("type")
    protected TransactionType type;

    @Field("from_accounts")
    protected Map<ObjectId, Double> fromAccounts;

    @Field("to_accounts")
    protected Map<ObjectId, Double> toAccounts;

    @Field("total_amount")
    protected Double totalAmount;

    @Field("category")
    protected String category;

    @Field("tags")
    protected Set<String> tags;

    public static <B extends BaseTransactionEntityBuilder<?, ?>, D extends BaseTransaction>
            B populateBaseTransactionEntityFields(B builder, D transaction) {
        if (transaction == null) {
            return builder;
        }

        builder.name(transaction.getName())
                .description(transaction.getDescription())
                .type(transaction.getType())
                .fromAccounts(
                        CollectionTransformer.transformMapKeys(
                                transaction.getFromAccounts().orElse(Map.of()),
                                MongoUtils::idToEntity))
                .toAccounts(
                        CollectionTransformer.transformMapKeys(
                                transaction.getToAccounts().orElse(Map.of()),
                                MongoUtils::idToEntity))
                .totalAmount(transaction.getTotalAmount())
                .category(transaction.getCategory())
                .tags(transaction.getTags().orElse(Set.of()));
        return populateAuditableEntityFields(builder, transaction);
    }

    public <B extends BaseTransaction.BaseTransactionBuilder<?, ?>>
            B populateBaseTransactionDomainFields(B builder) {
        builder.name(name)
                .description(description)
                .type(type)
                .fromAccounts(
                        CollectionTransformer.transformMapKeys(
                                fromAccounts, MongoUtils::idToDomain))
                .toAccounts(
                        CollectionTransformer.transformMapKeys(toAccounts, MongoUtils::idToDomain))
                .totalAmount(totalAmount)
                .category(category)
                .tags(Optional.ofNullable(tags).orElse(Set.of()));
        return populateAuditableDomainFields(builder);
    }
}
