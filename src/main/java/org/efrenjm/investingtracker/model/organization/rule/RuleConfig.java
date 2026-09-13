package org.efrenjm.investingtracker.model.organization.rule;

import lombok.*;
import org.efrenjm.investingtracker.model.transaction.AccountAllocation;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RuleConfig {
    private String type;

    private Boolean automatic;

    @Field("from_accounts")
    private List<AccountAllocation> fromAccounts;

    @Field("to_accounts")
    private List<AccountAllocation> toAccounts;

    @Field("total_amount")
    private Double totalAmount;

    @Field("transaction_categories")
    private List<String> transactionCategories;

    @Field("initial_date")
    private Date initialDate;

    private Periodicity periodicity;
}
