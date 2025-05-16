package org.efrenjm.investingtracker.domain.dto;

import lombok.*;
import org.efrenjm.investingtracker.domain.model.account.Account;
import org.efrenjm.investingtracker.domain.model.account.AccountConfig;
import org.efrenjm.investingtracker.domain.model.account.AccountType;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class AccountSummary {
	private String id;
	private String name;
	private String description;
	private AccountType type;
	private Double available;
	private List<String> tags;
	private AccountConfig accountConfig;

	public AccountSummary(Account account) {
		this.id = account.getId();
		this.name = account.getName();
		this.description = account.getDescription();
		this.type = account.getType();
		this.available = account.getAvailable();
		this.tags = account.getTags();
		this.accountConfig = account.getAccountConfig();
	}
}
