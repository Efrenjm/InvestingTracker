package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BasePasswordRequestDTO {
	@NotNull(message = "New password can't be null")
	@NotBlank(message = "New password can't be empty")
	private String newPassword;

	@NotNull(message = "Old password can't be null")
	@NotBlank(message = "Old password can't be empty")
	private String confirmPassword;

	@AssertTrue(message = "Passwords doesn't match")
	public boolean arePasswordsMatching() {
		return newPassword != null && newPassword.equals(confirmPassword);
	}
}