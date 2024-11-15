package org.efrenjm.investingtracker.model.Rule;

import lombok.*;
import org.efrenjm.investingtracker.model.Account.Account;

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
    private List<Account> fromAccounts;
    private List<Account> toAccounts;
    private Double totalAmount;
    private List<String> transactionCategories;
    private LocalDate initialDate;
    private Periodicity periodicity;
}
