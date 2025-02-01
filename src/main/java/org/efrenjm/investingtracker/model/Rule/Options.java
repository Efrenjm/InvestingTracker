package org.efrenjm.investingtracker.model.Rule;

import lombok.*;
import org.efrenjm.investingtracker.model.Account.Account;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Options {
    private String type;

    private Boolean automatic;

    @Field("from_accounts")
    private List<Account> fromAccounts;

    @Field("to_accounts")
    private List<Account> toAccounts;

    @Field("total_amount")
    private Double totalAmount;

    @Field("transaction_categories")
    private List<String> transactionCategories;

    @Field("initial_date")
    private LocalDate initialDate;

    private Periodicity periodicity;
}
