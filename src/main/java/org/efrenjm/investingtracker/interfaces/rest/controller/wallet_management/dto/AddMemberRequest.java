package org.efrenjm.investingtracker.interfaces.rest.controller.wallet_management.dto;

import jakarta.validation.constraints.NotBlank;

public record AddMemberRequest(
		@NotBlank String memberId,
		@NotBlank String roleName
) {}
