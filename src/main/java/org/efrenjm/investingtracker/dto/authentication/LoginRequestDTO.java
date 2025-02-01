package org.efrenjm.investingtracker.dto.authentication;

import lombok.Getter;

@Getter
public class LoginRequestDTO {
	private String email;
	private String phone;
	private String username;
	private String password;
}
