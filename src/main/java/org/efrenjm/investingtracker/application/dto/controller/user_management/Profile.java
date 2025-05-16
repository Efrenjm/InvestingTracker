package org.efrenjm.investingtracker.application.dto.controller.user_management;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.efrenjm.investingtracker.domain.model.user.User;

@RequiredArgsConstructor
@Getter
@ToString
public class Profile {
	String id;
	String email;
	String phoneNumber;
	String firstName;
	String middleName;
	String lastName;
	String profilePicture;

	public Profile(User user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.phoneNumber = user.getPhoneNumber();
		this.firstName = user.getFirstName();
		this.middleName = user.getMiddleName();
		this.lastName = user.getLastName();
		this.profilePicture = user.getProfilePicture();
	}
}