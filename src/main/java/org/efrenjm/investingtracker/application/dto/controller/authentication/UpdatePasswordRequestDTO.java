package org.efrenjm.investingtracker.application.dto.controller.authentication;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.efrenjm.investingtracker.application.service.utils.ValidationService;

@Getter
@Setter
public class UpdatePasswordRequestDTO extends BasePasswordRequestDTO {
	@NotNull(message = "Password cannot be null")
	@NotBlank(message = "Password cannot be empty")
	private String oldPassword;

	public UpdatePasswordRequestDTO(ValidationService validationService) {
		super(validationService);
	}
}
