package org.efrenjm.investingtracker.dto.controller.authentication;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterRequestDTO {
	@Email(message = "Email must be a valid format")
	private String email;

	@Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone must be a valid format")
	private String phone;

	@NotNull(message = "Password cannot be null")
	@NotBlank(message = "Password cannot be empty")
	@Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters long")
	@Pattern(
			regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
			message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
	)
	private String password;

	@AssertTrue(message = "An email or phone must be provided")
	public boolean isEmailOrPhonePresent() {
		return email != null || phone != null;
	}
}
