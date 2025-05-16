package org.efrenjm.investingtracker.application.dto.controller.authentication;

import org.efrenjm.investingtracker.application.service.utils.ValidationService;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequestDTO extends BasePasswordRequestDTO {
	protected ForgotPasswordRequestDTO(ValidationService validationService) {
		super(validationService);
	}
}
