package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto;

import lombok.Getter;
import lombok.Setter;
import org.efrenjm.investingtracker.application.service.authentication.exceptions.DefaultRegistrationException;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.user.VerificationRequest;

import java.util.Optional;

@Getter
@Setter
public class RegisterResponseDTO {
	private String userId;
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
