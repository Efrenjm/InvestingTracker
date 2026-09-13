package org.efrenjm.investingtracker.model.account;

import lombok.*;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.model.organization.Organization;
import org.efrenjm.investingtracker.model.rule.Rule;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "accounts")
public class Account {
    @Id
    @Field("_id")
    private ObjectId id;

    private Organization organization;

    private String name;

    private String type;

    private Double available;

    private String description;

    private List<String> tags;

    @Field("created_at")
    private ZonedDateTime createdAt;

    @Field("updated_at")
    private ZonedDateTime updatedAt;

    private Configuration configuration;

    private List<Rule> rules;

    /* Debit */
    private Double goal;

    /* Assets */
    private String asset;

    @Field("current_price")
    private Double currentPrice;

    @Field("unit_cost")
    private Double unitCost;

    /* Credit */
    @Field("current_debt")
    private Double currentDebt;

    @Field("credit_limit")
    private Double creditLimit;
}
