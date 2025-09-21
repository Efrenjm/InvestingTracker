package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto;

import lombok.Getter;
import lombok.Setter;
import org.efrenjm.investingtracker.domain.model.user.User;

@Getter
@Setter
public class VerifyCodeResponseDTO
{
	private String userId;
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
