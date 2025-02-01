package org.efrenjm.investingtracker.model.Transaction;

import lombok.*;
import org.efrenjm.investingtracker.model.Account.Account;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    @Id
    @Field("_id")
    private String id;

    private String name;

    private String description;

    private String type;

    @Field("from_accounts")
    private List<Account> fromAccounts;

    @Field("to_accounts")
    private List<Account> toAccounts;

    @Field("total_amount")
    private Double totalAmount;

    private List<String> categories;

    @Field("transaction_date")
    private LocalDate transactionDate;

    @Field("created_at")
    private ZonedDateTime createdAt;

    @Field("updated_at")
    private ZonedDateTime updatedAt;
}
