package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Request payload used to initiate registration with an email address or phone number.")
public class RegisterRequestDTO {
	@Schema(
			description = "Email address or phone number for registration.",
			example = "john.doe@email.com",
			requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotNull(message = "Username must be provided")
	@NotBlank(message = "Username can't be empty")
	private String username;
}
