package org.efrenjm.investingtracker.domain.model.transaction;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@SuperBuilder
@Getter
@Setter
@ToString
public class Transaction extends BaseTransaction
{
    private Date transactionDate;
}
