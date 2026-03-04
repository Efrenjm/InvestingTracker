package org.efrenjm.investingtracker.domain.dto;

import org.efrenjm.investingtracker.domain.model.user.UserPreferences;

/**
 * Command object for updating a user's profile.
 * This is a domain-level DTO that represents the data needed to update a profile,
 * independent of the transport layer (HTTP, etc.).
 */
public record ProfileUpdateCommand(
		String username,
		String firstName,
		String middleName,
		String lastName,
		String profilePicture,
		UserPreferences userPreferences
) {
	/**
	 * Creates a builder-like method for partial updates.
	 * Returns a new command with non-null values from this command,
	 * falling back to current user values for null fields.
	 */
	public ProfileUpdateCommand withDefaults(
			String currentUsername,
			String currentFirstName,
			String currentMiddleName,
			String currentLastName,
			String currentProfilePicture,
			UserPreferences currentPreferences
	) {
		return new ProfileUpdateCommand(
				username != null ? username : currentUsername,
				firstName != null ? firstName : currentFirstName,
				middleName != null ? middleName : currentMiddleName,
				lastName != null ? lastName : currentLastName,
				profilePicture != null ? profilePicture : currentProfilePicture,
				userPreferences != null ? userPreferences : currentPreferences
		);
	}
}

