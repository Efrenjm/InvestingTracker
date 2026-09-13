package org.efrenjm.investingtracker.model.user;

import lombok.Getter;

@Getter
public enum CodeUsage {
	EMAIL_VERIFICATION("email-verification"),
	PHONE_VERIFICATION("phone-verification");

	private final String value;

	CodeUsage(String value) {
		this.value = value;
	}

	public static CodeUsage fromValue(String value) {
		for (CodeUsage usage: CodeUsage.values()) {
			if (usage.value.equalsIgnoreCase(value)) {
				return usage;
			}
		}
		throw new IllegalArgumentException("Invalid TokenUsage value: " + value);
	}
}
