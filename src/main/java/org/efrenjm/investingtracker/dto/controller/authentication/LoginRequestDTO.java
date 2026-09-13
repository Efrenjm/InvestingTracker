package org.efrenjm.investingtracker.dto.controller.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginRequestDTO {
	private String email;
	private String phone;
	private String password;
}
