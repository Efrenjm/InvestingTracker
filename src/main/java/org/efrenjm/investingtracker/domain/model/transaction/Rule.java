package org.efrenjm.investingtracker.domain.model.transaction;

import lombok.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class Rule extends BaseTransaction {
	private Boolean automatic;
	private Date initialDate;
	private Periodicity periodicity;

	@Builder
	public Rule(String id, String name, String description, String type, List<AccountAllocation> fromAccounts, List<AccountAllocation> toAccounts, Double totalAmount, List<String> categories, List<String> tags, Date createdAt, Date updatedAt, Boolean automatic, Date initialDate, Periodicity periodicity) {
		super(id, name, description, type, fromAccounts, toAccounts, totalAmount, categories, tags, createdAt, updatedAt);
		this.automatic = automatic;
		this.initialDate = initialDate;
		this.periodicity = periodicity;
	}
}