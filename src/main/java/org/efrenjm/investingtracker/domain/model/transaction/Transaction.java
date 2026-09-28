package org.efrenjm.investingtracker.domain.model.transaction;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@ToString
public class Transaction extends BaseTransaction {
    private Date transactionDate;
}
