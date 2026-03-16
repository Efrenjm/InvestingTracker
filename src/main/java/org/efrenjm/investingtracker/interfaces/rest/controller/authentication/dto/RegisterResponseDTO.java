package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.efrenjm.investingtracker.application.service.authentication.exceptions.DefaultRegistrationException;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.user.VerificationRequest;

import java.util.Optional;

@Getter
@Setter
@Schema(description = "Response returned after successful user registration.")
public class RegisterResponseDTO {
	@Schema(
			description = "Identifier of the registered user.",
			example = "67d2f18d8b17c24e3fe46ed1",
			requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String userId;

	@Schema(
			description = "Credential where the verification code was sent.",
			example = "john.doe@email.com",
			requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String username;

	public RegisterResponseDTO(User user) {
		Optional<VerificationRequest> request = user.getVerificationRequest();
		if (request.isEmpty()) {
			throw new DefaultRegistrationException();
		}
		this.userId = user.getId();
		this.username = request.get().getCredential();
	}
}
