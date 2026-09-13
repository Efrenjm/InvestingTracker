package org.efrenjm.investingtracker.model.organization.account;

import lombok.Getter;
import org.efrenjm.investingtracker.model.user.CodeUsage;

@Getter
public enum AccountType {
	DEBIT("debit"),
	CREDIT("credit"),
	ASSET("asset");

	private final String value;

	AccountType(String value) {
		this.value = value;
	}

	public static AccountType fromValue(String value) {
		for (AccountType usage: AccountType.values()) {
			if (usage.value.equalsIgnoreCase(value)) {
				return usage;
			}
		}
		throw new IllegalArgumentException("Invalid AccountType value: " + value);
	}
}
