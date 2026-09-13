package org.efrenjm.investingtracker.dto.controller.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.efrenjm.investingtracker.model.user.User;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RegisterResponseDTO {
	private String userId;
	private String email;
	private String phone;

	public RegisterResponseDTO(User user) {
		this.userId = user.getId().toString();
		this.email = user.getUpdateEmailRequest();
		this.phone = user.getUpdatePhoneRequest();
	}
}
