package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequestDTO extends BasePasswordRequestDTO {
	@NotNull(message = "Username can't be null")
	@NotBlank(message = "Username can't be empty")
	private String username;
}
