package org.efrenjm.investingtracker.domain.model.transaction;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.efrenjm.investingtracker.domain.model.AuditableModel;

import java.util.*;

@SuperBuilder
@Getter
@Setter
@ToString
public abstract class BaseTransaction extends AuditableModel
{
	private String name;

	private String description;

	private TransactionType type;

	private Map<String, Double> fromAccounts;

	private Map<String, Double> toAccounts;

	private Double totalAmount;

	private String category;

	private Set<String> tags;

	public Optional<Map<String, Double>> getFromAccounts()
	{
		return Optional.ofNullable(fromAccounts);
	}

	public Optional<Map<String, Double>> getToAccounts()
	{
		return Optional.ofNullable(toAccounts);
	}

	public Optional<Set<String>> getTags()
	{
		return Optional.ofNullable(tags);
	}
}
