package org.efrenjm.investingtracker.dto.authentication;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class CompleteRegistrationRequestDTO {
	private ObjectId userId;
	private String token;
}
