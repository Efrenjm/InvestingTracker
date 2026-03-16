package org.efrenjm.investingtracker.domain.dto;

import org.efrenjm.investingtracker.domain.model.user.User;

public record PublicProfile (
	String id,
	String username,
	String email,
	String phoneNumber,
	String firstName,
	String middleName,
	String lastName,
	String profilePicture
)
{
	public static PublicProfile from(User user)
	{
		return new PublicProfile(
				user.getId(),
				user.getUsername(),
				user.getEmail(),
				user.getPhoneNumber(),
				user.getFirstName(),
				user.getMiddleName(),
				user.getLastName(),
				user.getProfilePicture()
		);
	}
}
