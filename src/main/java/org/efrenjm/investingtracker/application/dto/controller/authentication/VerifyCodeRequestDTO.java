package org.efrenjm.investingtracker.application.dto.controller.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyCodeRequestDTO {
	private String userId;

	@NotNull(message = "A valid code must be provided")
	@NotBlank(message = "Code cannot be blank")
	private String code;
}
