package org.efrenjm.investingtracker.domain.model.transaction;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;
import java.util.Map;

@SuperBuilder
@Getter
@Setter
@ToString
public class Rule extends BaseTransaction
{
	private Boolean automatic;

	private Date initialDate;

	private Map<Period, Integer> periodicity;
}