package org.efrenjm.investingtracker.domain.dto;

import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public record Profile(
		String id,
		String username,
		String email,
		String phoneNumber,
		String firstName,
		String middleName,
		String lastName,
		String profilePicture,
		Set<SystemRole> roles
)
{
	public static Profile from(User user)
	{
		return new Profile(
				user.getId(),
				user.getUsername(),
				user.getEmail(),
				user.getPhoneNumber(),
				user.getFirstName(),
				user.getMiddleName(),
				user.getLastName(),
				user.getProfilePicture(),
				user.getRoles()
		);
	}
}
