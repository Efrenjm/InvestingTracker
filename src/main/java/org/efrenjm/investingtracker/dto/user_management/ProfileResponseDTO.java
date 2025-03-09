package org.efrenjm.investingtracker.dto.user_management;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.efrenjm.investingtracker.model.profile.Profile;

@RequiredArgsConstructor
@Getter
@ToString
public class ProfileResponseDTO {
	String id;
	String email;
	String phoneNumber;
	String firstName;
	String middleName;
	String lastName;
	String profilePicture;

	public ProfileResponseDTO(Profile profile) {
		this.id = profile.getId().toString();
		this.email = profile.getEmail();
		this.phoneNumber = profile.getPhoneNumber();
		this.firstName = profile.getFirstName();
		this.middleName = profile.getMiddleName();
		this.lastName = profile.getLastName();
		this.profilePicture = profile.getProfilePicture();
	}
}