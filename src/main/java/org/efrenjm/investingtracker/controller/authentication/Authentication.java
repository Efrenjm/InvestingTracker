package org.efrenjm.investingtracker.controller.authentication;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.efrenjm.investingtracker.dto.authentication.LoginRequestDTO;
import org.efrenjm.investingtracker.dto.authentication.RegisterRequestDTO;
import org.efrenjm.investingtracker.service.user_management.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class Authentication {
	private final AuthenticationService authenticationService;

	@PostMapping("/login")
	public Mono<ResponseEntity<String>> login(@RequestBody LoginRequestDTO loginRequest) {
		return authenticationService.login(loginRequest.getEmail(), loginRequest.getPhone(), loginRequest.getPassword())
				.map(ResponseEntity::ok);
	}

	@PostMapping("/register")
	public Mono<ResponseEntity<String>> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
		return authenticationService.register(registerRequest)
				.map(user -> ResponseEntity.created(URI.create("/profile/" + user.getId())).build());
	}
}

