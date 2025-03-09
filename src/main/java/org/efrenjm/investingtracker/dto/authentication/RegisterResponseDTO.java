package org.efrenjm.investingtracker.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RegisterResponseDTO {
	private String userId;
	private String email;
	private String phone;
}
