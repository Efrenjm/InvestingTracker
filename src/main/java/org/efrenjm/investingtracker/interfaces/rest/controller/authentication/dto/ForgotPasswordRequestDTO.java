package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Payload used to request a password reset for an existing account.")
public class ForgotPasswordRequestDTO extends BasePasswordRequestDTO {
	@Schema(
			description = "Username, email, or phone number that identifies the account.",
			example = "john.doe@email.com",
			requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotNull(message = "Username can't be null")
	@NotBlank(message = "Username can't be empty")
	private String username;
}
