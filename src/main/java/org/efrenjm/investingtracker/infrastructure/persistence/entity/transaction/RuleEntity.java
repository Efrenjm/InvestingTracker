package org.efrenjm.investingtracker.infrastructure.persistence.entity.transaction;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.efrenjm.investingtracker.domain.model.transaction.Period;
import org.efrenjm.investingtracker.domain.model.transaction.Rule;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
@Document(collection = "rules")
public class RuleEntity extends BaseTransactionEntity
{
	@Field("automatic") private Boolean automatic;
	@Field("initial_date") private Date initialDate;
	@Field("periodicity") private Map<Period, Integer> periodicity;

	public static RuleEntity fromDomain(Rule rule)
	{
		if (rule == null)
			return null;

		return populateBaseTransactionEntityFields(RuleEntity.builder(), rule)
				.automatic(rule.getAutomatic())
				.initialDate(rule.getInitialDate())
				.periodicity(rule.getPeriodicity())
				.build();
	}

	public Rule toDomain()
	{
		return populateBaseTransactionDomainFields(Rule.builder())
				.automatic(automatic)
				.initialDate(initialDate)
				.periodicity(periodicity)
				.build();
	}
}