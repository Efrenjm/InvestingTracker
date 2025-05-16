package org.efrenjm.investingtracker.application.dto.controller.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginRequestDTO {
	@NotNull(message = "Username must be provided")
	@NotBlank(message = "Username can't be empty")
	private String username;
	@NotNull(message = "Password must be provided")
	@NotBlank(message = "Password can't be empty")
	private String password;
}
