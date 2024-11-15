package org.efrenjm.investingtracker.model.Transaction;

import lombok.*;
import org.efrenjm.investingtracker.model.Account.Account;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "transactions")
public class Transaction {
    @Id private String id;
    private String name;
    private String description;
    private String type;
    private List<Account> fromAccounts;
    private List<Account> toAccounts;
    private Double totalAmount;
    private List<String> categories;
    private LocalDate transactionDate;
    private ZonedDateTime created_at;
    private ZonedDateTime updated_at;
}
