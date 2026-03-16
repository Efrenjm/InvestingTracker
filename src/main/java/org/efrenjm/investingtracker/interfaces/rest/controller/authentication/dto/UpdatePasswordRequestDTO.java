package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Payload used by authenticated users to change their current password.")
public class UpdatePasswordRequestDTO extends BasePasswordRequestDTO {
	@Schema(
			description = "Current password used to authorize the password change.",
			example = "Curr3ntP@ss!",
			requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotNull(message = "Password cannot be null")
	@NotBlank(message = "Password cannot be empty")
	private String oldPassword;
}
