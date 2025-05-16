package org.efrenjm.investingtracker.domain.dto;

import lombok.*;
import org.bson.types.ObjectId;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class PublicProfile {
	private ObjectId id;
	private String username;
	private String firstName;
	private String middleName;
	private String lastName;
	private String profilePicture;
}
