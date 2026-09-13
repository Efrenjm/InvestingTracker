package org.efrenjm.investingtracker.dto.controller.user_management;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.efrenjm.investingtracker.model.user.User;

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

	public ProfileResponseDTO(User user) {
		this.id = user.getId().toString();
		this.email = user.getEmail();
		this.phoneNumber = user.getPhoneNumber();
		this.firstName = user.getFirstName();
		this.middleName = user.getMiddleName();
		this.lastName = user.getLastName();
		this.profilePicture = user.getProfilePicture();
	}
}