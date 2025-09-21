package org.efrenjm.investingtracker.domain.dto;

import lombok.*;
import org.efrenjm.investingtracker.domain.model.account.AccountConfig;
import org.efrenjm.investingtracker.domain.model.account.AccountType;
import org.efrenjm.investingtracker.domain.model.account.BaseAccount;

import java.util.Set;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class AccountSummary
{
	private String id;
	private String name;
	private String description;
	private AccountType type;
	private Double available;
	private Set<String> tags;
	private AccountConfig accountConfig;

	public AccountSummary(BaseAccount account)
	{
		this.id = account.getId();
		this.name = account.getName();
		this.description = account.getDescription();
		this.type = account.getType();
		this.available = account.getAvailable();
		this.tags = account.getTags().orElse(Set.of());
		this.accountConfig = account.getAccountConfig();
	}
}
