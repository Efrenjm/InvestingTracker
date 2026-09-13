package org.efrenjm.investingtracker.model.organization.transaction_category;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TransactionCategory {
    private String name;

    private String color;

    private String icon;

    private String type;
}
