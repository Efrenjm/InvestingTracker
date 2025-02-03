package org.efrenjm.investingtracker.model.transaction_category;

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
