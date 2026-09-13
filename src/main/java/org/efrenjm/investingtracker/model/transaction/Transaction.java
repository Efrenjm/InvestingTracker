package org.efrenjm.investingtracker.model.transaction;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
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
    private List<AccountAllocation> fromAccounts;

    @Field("to_accounts")
    private List<AccountAllocation> toAccounts;

    @Field("total_amount")
    private Double totalAmount;

    private List<String> categories;

    @Field("transaction_date")
    private Date transactionDate;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;
}
