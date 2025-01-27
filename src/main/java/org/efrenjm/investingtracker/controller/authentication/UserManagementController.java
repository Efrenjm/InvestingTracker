package org.efrenjm.investingtracker.controller.authentication;

import org.efrenjm.investingtracker.controller.authentication.models.AuthRequestDTO;
import org.efrenjm.investingtracker.controller.authentication.models.RegisterRequestDTO;
import org.efrenjm.investingtracker.service.user_management.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class UserManagementController {
	@Autowired
	private UserManagementService userManagementService;

	@PostMapping("/login")
	public Mono<ResponseEntity<String>> getAllAccounts(@RequestBody AuthRequestDTO authRequest) {
		return userManagementService.login(authRequest.getUsername(), authRequest.getPassword())
				.map(ResponseEntity::ok)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")));
	}

	@PostMapping("/register")
	public Mono<ResponseEntity<String>> register(@RequestBody RegisterRequestDTO registerRequest) {
		return userManagementService.register(registerRequest.getUsername(), registerRequest.getPassword())
				.map(user -> ResponseEntity.created(URI.create("/auth/" + user.getId())).build());
	}
}

