package org.efrenjm.investingtracker.model.account;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.model.organization.Organization;
import org.efrenjm.investingtracker.model.rule.Rule;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Document(collection = "accounts")
public class Account {
    @Id
    @Field("_id")
    private ObjectId id;

    @DocumentReference
    @JsonBackReference
    private Organization organization;

    private String name;

    private String description;

    private String type;

    private Double available;

    private List<String> tags;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;

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
