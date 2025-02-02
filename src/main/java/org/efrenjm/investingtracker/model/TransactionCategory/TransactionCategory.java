package org.efrenjm.investingtracker.model.TransactionCategory;

import lombok.*;
import org.efrenjm.investingtracker.model.Profile.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "transaction_categories")
public class TransactionCategory {
    @Id
    @Field("_id")
    private String id;

    private Profile owner;
    private List<Category> category;
}
