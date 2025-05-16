package org.efrenjm.investingtracker.domain.model.wallet;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class TransactionCategory {
    private String name;
    private String color;
    private String icon;
    private String type;
}
