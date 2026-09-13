package org.efrenjm.investingtracker.dto.controller.account_management;

import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.efrenjm.investingtracker.model.organization.account.AccountConfig;
import org.efrenjm.investingtracker.model.organization.account.AccountType;

import java.util.List;

public class CreateAccountRequestDTO {
	@NotNull
	@NotBlank
	private String name;

	private String description;

	@NotBlank
	@NotNull
	private String type;

	private Double available;

	private List<String> tags;

	private AccountConfig config;

	/* Debit */
	private Double goal;

	/* Assets */
	private String asset;

	private Double currentPrice;

	private Double averageCost;

	/* Credit */
	private Double currentDebt;

	private Double creditLimit;

	@AssertFalse(message = "Asset accounts must have an asset name, and an average cost")
	public boolean isAssetPropertiesMissing() {
		if (type.equals(AccountType.ASSET.getValue())) {
			return asset == null || averageCost == null;
		}
		return false;
	}

	@AssertFalse(message = "Credit accounts must have a current debt")
	public boolean isCreditPropertiesMissing() {
		if (type.equals(AccountType.DEBIT.getValue())) {
			return currentDebt != null;
		}
		return false;
	}
}