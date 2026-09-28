package org.efrenjm.investingtracker.domain.model.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public class DebitAccount extends BaseAccount {
    private Double goal;
}
