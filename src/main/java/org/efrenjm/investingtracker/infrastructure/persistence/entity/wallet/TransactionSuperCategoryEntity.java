package org.efrenjm.investingtracker.infrastructure.persistence.entity.wallet;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.efrenjm.investingtracker.domain.model.wallet.TransactionSuperCategory;
import org.efrenjm.investingtracker.domain.utils.CollectionTransformer;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public class TransactionSuperCategoryEntity extends BaseTransactionCategoryEntity {
    @Field("subcategories")
    private Map<String, TransactionCategoryEntity> subcategories;

    public static TransactionSuperCategoryEntity fromDomain(TransactionSuperCategory category) {
        if (category == null) {
            return null;
        }

        return populateBaseEntityFields(TransactionSuperCategoryEntity.builder(), category)
                .subcategories(
                        CollectionTransformer.transformMapValues(
                                category.getSubcategories(), TransactionCategoryEntity::fromDomain))
                .build();
    }

    public TransactionSuperCategory toDomain() {
        return populateBaseDomainFields(TransactionSuperCategory.builder())
                .subcategories(
                        CollectionTransformer.transformMapValues(
                                subcategories, TransactionCategoryEntity::toDomain))
                .build();
    }
}
