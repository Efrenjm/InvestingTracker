package org.efrenjm.investingtracker.dto.controller.authentication;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdatePasswordRequestDTO {
	@NotNull
	private String oldPassword;

	@NotNull(message = "Password cannot be null")
	@NotBlank(message = "Password cannot be empty")
	@Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters long")
	@Pattern(
			regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
			message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
	)
	private String newPassword;

	@NotNull
	private String confirmPassword;

	@AssertTrue(message = "Passwords doesn't match")
	public boolean isPasswordMatching() {
		return newPassword != null && newPassword.equals(confirmPassword);
	}

	@AssertFalse(message = "The new password cannot be the same as the current password")
	public boolean isSamePassword() {
		return newPassword != null && newPassword.equals(oldPassword);
	}
}
