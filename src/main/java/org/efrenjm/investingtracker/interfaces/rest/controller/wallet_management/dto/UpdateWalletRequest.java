package org.efrenjm.investingtracker.interfaces.rest.controller.wallet_management.dto;

import jakarta.validation.constraints.NotBlank;
import org.efrenjm.investingtracker.domain.model.wallet.Visibility;

public record UpdateWalletRequest(
		@NotBlank String name,
		String description,
		Visibility visibility
) {}
