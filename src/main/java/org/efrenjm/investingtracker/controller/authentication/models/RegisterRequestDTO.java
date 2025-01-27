package org.efrenjm.investingtracker.controller.authentication.models;

import lombok.Getter;

@Getter
public class RegisterRequestDTO {
	private String username;
	private String password;
	private String email;
}
