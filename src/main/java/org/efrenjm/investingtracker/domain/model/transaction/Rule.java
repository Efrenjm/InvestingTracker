package org.efrenjm.investingtracker.domain.model.transaction;

import java.util.Date;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@ToString
public class Rule extends BaseTransaction {
    private Boolean automatic;

    private Date initialDate;

    private Map<Period, Integer> periodicity;
}
