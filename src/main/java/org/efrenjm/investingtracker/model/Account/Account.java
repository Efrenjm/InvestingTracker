package org.efrenjm.investingtracker.model.Account;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.efrenjm.investingtracker.model.Organization.Organization;
import org.efrenjm.investingtracker.model.Rule.Rule;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Document(collection = "accounts")
public class Account {
    @Id
    private String _id;
    private Organization organization;
    private String name;
    private String type;
    private Double available;
    private String description;
    private List<String> tags;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private Configuration configuration;
    private List<Rule> rules;

    /* Debit */
    private Double goal;

    /* Assets */
    private String asset;
    private Double unitCost;
    private Double currentPrice;

    /* Credit */
    private Double currentDebt;
    private Double creditLimit;
}
