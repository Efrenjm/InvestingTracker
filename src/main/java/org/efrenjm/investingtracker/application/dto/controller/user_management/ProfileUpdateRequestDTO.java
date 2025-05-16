package org.efrenjm.investingtracker.application.dto.controller.user_management;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ProfileUpdateRequestDTO {
	private String username;
	private String firstName;
	private String middleName;
	private String lastName;
	private String profilePicture;
}
