package org.efrenjm.investingtracker.dto.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Setter
@Getter
@RequiredArgsConstructor
public class PublicProfile {
	@Id
	private ObjectId id;

	private String username;

	private String email;

	private String phoneNumber;

	private Set<GrantedAuthority> roles = new HashSet<>();

	private String firstName;

	private String middleName;

	private String lastName;

	private String profilePicture;
}
