package org.efrenjm.investingtracker.model.Rule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.efrenjm.investingtracker.model.Account.Account;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
