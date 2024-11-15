package org.efrenjm.investingtracker.model.Rule;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Periodicity {
    private int amount;
    private String period;
}
