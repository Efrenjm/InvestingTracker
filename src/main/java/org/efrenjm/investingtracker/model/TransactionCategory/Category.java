package org.efrenjm.investingtracker.model.TransactionCategory;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Category {
    private String name;

    private String color;

    private String icon;

    private String type;
}
