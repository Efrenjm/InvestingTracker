package org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections;

import lombok.*;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.dto.PublicProfile;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class PublicProfileProjection {
	@Id
	private ObjectId id;

	private String username;

	private String firstName;

	private String middleName;

	private String lastName;

	private String profilePicture;

	public static PublicProfileProjection fromDomain(PublicProfile projection) {
		return PublicProfileProjection.builder()
				.id(projection.getId())
				.username(projection.getUsername())
				.firstName(projection.getFirstName())
				.middleName(projection.getMiddleName())
				.lastName(projection.getLastName())
				.profilePicture(projection.getProfilePicture())
				.build();
	}
	public PublicProfile toDomain() {
		return PublicProfile.builder()
				.id(id)
				.username(username)
				.firstName(firstName)
				.middleName(middleName)
				.lastName(lastName)
				.profilePicture(profilePicture)
				.build();
	}
}
