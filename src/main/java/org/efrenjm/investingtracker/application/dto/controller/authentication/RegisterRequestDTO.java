package org.efrenjm.investingtracker.application.dto.controller.authentication;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterRequestDTO {
	@NotNull(message = "Username must be provided")
	@NotBlank(message = "Username can't be empty")
	private String username;

	@NotNull(message = "Password must be provided")
	@NotBlank(message = "Password can't be empty")
	@Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters long")
	@Pattern(
			regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
			message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
	)
	private String password;
}
