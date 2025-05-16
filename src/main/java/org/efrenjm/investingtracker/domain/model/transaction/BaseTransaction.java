package org.efrenjm.investingtracker.domain.model.transaction;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public abstract class BaseTransaction {
	private String id;
	private String name;
	private String description;
	private String type;
	private List<AccountAllocation> fromAccounts;
	private List<AccountAllocation> toAccounts;
	private Double totalAmount;
	private List<String> categories;
	private List<String> tags;
	private Date createdAt;
	private Date updatedAt;

	public Optional<List<AccountAllocation>> getFromAccounts() {
		return Optional.ofNullable(fromAccounts);
	}

	public Optional<List<AccountAllocation>> getToAccounts() {
		return Optional.ofNullable(toAccounts);
	}
}
