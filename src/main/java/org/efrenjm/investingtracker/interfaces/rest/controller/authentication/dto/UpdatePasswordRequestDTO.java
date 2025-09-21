package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequestDTO extends BasePasswordRequestDTO {
	@NotNull(message = "Password cannot be null")
	@NotBlank(message = "Password cannot be empty")
	private String oldPassword;
}
