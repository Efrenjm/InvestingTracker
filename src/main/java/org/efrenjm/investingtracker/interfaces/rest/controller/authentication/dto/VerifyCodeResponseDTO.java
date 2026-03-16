package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.efrenjm.investingtracker.domain.model.user.User;

@Getter
@Setter
@Schema(description = "Response returned after successful verification of a code.")
public class VerifyCodeResponseDTO
{
	@Schema(
			description = "Identifier of the verified user.",
			example = "67d2f18d8b17c24e3fe46ed1",
			requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String userId;

	@Schema(
			description = "Verified credential (phone or email) associated with the account.",
			example = "john.doe@email.com",
			requiredMode = Schema.RequiredMode.REQUIRED
	)
	private String username;

	public VerifyCodeResponseDTO(User user)
	{
		this.userId = user.getId();
		if (user.getPhoneNumber() != null)
		{
			this.username = user.getPhoneNumber();
		}
		else
		{
			this.username = user.getEmail();
		}
	}
}
