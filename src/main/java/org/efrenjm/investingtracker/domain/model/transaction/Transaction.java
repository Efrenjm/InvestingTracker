package org.efrenjm.investingtracker.domain.model.transaction;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class Transaction extends BaseTransaction {
    private Date transactionDate;

    @Builder
    public Transaction(String id, String name, String description, String type, List<AccountAllocation> fromAccounts, List<AccountAllocation> toAccounts, Double totalAmount, List<String> categories, List<String> tags, Date createdAt, Date updatedAt, Date transactionDate) {
        super(id, name, description, type, fromAccounts, toAccounts, totalAmount, categories, tags, createdAt, updatedAt);
        this.transactionDate = transactionDate;
    }
}
