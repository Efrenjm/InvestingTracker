package org.efrenjm.investingtracker.dto.model.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.model.organization.account.Account;
import org.efrenjm.investingtracker.model.organization.account.AccountConfig;
import org.efrenjm.investingtracker.model.organization.account.AccountType;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
public class AccountSummary {
	private ObjectId id;

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
