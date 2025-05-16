package org.efrenjm.investingtracker.application.dto.controller.authentication;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.efrenjm.investingtracker.application.service.utils.ValidationService;

@Getter
@Setter
public abstract class BasePasswordRequestDTO {
	@NotNull(message = "Username can't be null")
	@NotBlank(message = "Username can't be empty")
	private String username;

	@NotNull(message = "New password can't be null")
	@NotBlank(message = "New password can't be empty")
	private String newPassword;

	@NotNull(message = "Old password can't be null")
	@NotBlank(message = "Old password can't be empty")
	private String confirmPassword;

	protected final ValidationService validationService;

	protected BasePasswordRequestDTO(ValidationService validationService) {
		this.validationService = validationService;
	}

	@AssertTrue(message = "Passwords doesn't match")
	public boolean arePasswordsMatching() {
		return newPassword != null && newPassword.equals(confirmPassword);
	}

	@AssertTrue(message = "Invalid password. It should be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one number, and one special character.")
	public boolean isPasswordValid() {
		return validationService.isValidPassword(newPassword);
	}
}