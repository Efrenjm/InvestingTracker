package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserPasswordDTO {
	@NotNull(message = "Username must be provided")
	@NotBlank(message = "Username can't be empty")
	private String username;
	@NotNull(message = "Password must be provided")
	@NotBlank(message = "Password can't be empty")
	private String password;
}
