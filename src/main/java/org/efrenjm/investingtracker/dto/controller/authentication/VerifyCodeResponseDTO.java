package org.efrenjm.investingtracker.dto.controller.authentication;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.efrenjm.investingtracker.model.user.User;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class VerifyCodeResponseDTO {
	private String userId;
	private String email;
	private String phone;

	public VerifyCodeResponseDTO(User user) {
		this.userId = user.getId().toString();
		this.email = user.getEmail();
		this.phone = user.getPhoneNumber();
	}
}
