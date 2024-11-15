package org.efrenjm.investingtracker.model.TransactionCategory;

import org.efrenjm.investingtracker.model.Profile.PublicProfile;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "transaction_categories")
public class TransactionCategory {
    private String id;
    private PublicProfile owner;
    private List<Category> category;
}
