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

//	public void setName(String name, String updaterUserId)
//	{
//		this.name = name;
//		touchTransaction(updaterUserId);
//	}
//
//	public void setDescription(String description, String updaterUserId)
//	{
//		this.description = description;
//		touchTransaction(updaterUserId);
//	}
//
//	public void setType(TransactionType type, String updaterUserId)
//	{
//		this.type = type;
//		touchTransaction(updaterUserId);
//	}
//
//	public void setFromAccounts(Map<String, Double> fromAccounts, String updaterUserId)
//	{
//		this.fromAccounts = fromAccounts;
//		touchTransaction(updaterUserId);
//	}
//
//	public void setToAccounts(Map<String, Double> toAccounts, String updaterUserId)
//	{
//		this.toAccounts = toAccounts;
//		touchTransaction(updaterUserId);
//	}
//
//	public void setTotalAmount(Double totalAmount, String updaterUserId)
//	{
//		this.totalAmount = totalAmount;
//		touchTransaction(updaterUserId);
//	}
//
//	public void setCategories(String category, String updaterUserId)
//	{
//		this.category = category;
//		touchTransaction(updaterUserId);
//	}
//
//	public void setTags(Set<String> tags, String updaterUserId)
//	{
//		this.tags = tags;
//		touchTransaction(updaterUserId);
//	}
//
//	protected void touchTransaction(String updaterUserId)
//	{
//		this.updatedAt = new Date();
//		this.updatedBy = updaterUserId;
//	}
}
