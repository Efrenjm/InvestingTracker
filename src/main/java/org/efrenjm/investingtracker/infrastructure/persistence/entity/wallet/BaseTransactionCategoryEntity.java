package org.efrenjm.investingtracker.infrastructure.persistence.entity.wallet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.efrenjm.investingtracker.domain.model.wallet.BaseTransactionCategory;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public abstract class BaseTransactionCategoryEntity {
    @Field("description")
    protected String description;

    @Field("color")
    protected String color;

    @Field("icon")
    protected String icon;

    public static <
                    B extends BaseTransactionCategoryEntityBuilder<?, ?>,
                    D extends BaseTransactionCategory>
            B populateBaseEntityFields(B builder, D category) {
        if (category == null) {
            return builder;
        }

        builder.description(category.getDescription())
                .color(category.getColor())
                .icon(category.getIcon());
        return builder;
    }

    public <B extends BaseTransactionCategory.BaseTransactionCategoryBuilder<?, ?>>
            B populateBaseDomainFields(B builder) {
        builder.description(description).color(color).icon(icon);
        return builder;
    }
}
