package org.efrenjm.investingtracker.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class CompleteRegistrationResponseDTO {
	private String profileId;
	private String email;
	private String phone;
}
