package org.efrenjm.investingtracker.model.organization.account;

import lombok.*;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.model.organization.rule.Rule;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Account {
    @Field("_id")
    private ObjectId id;

    private String name;

    private String description;

    private AccountType type;

    private Double available;

    private List<String> tags;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;

    @Field("account_config")
    private AccountConfig accountConfig;

    private List<Rule> rules;

    /* Debit */
    private Double goal;

    /* Assets */
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
}
