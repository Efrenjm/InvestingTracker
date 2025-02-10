package org.efrenjm.investingtracker.controller.authentication;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.efrenjm.investingtracker.dto.authentication.LoginRequestDTO;
import org.efrenjm.investingtracker.dto.authentication.RegisterRequestDTO;
import org.efrenjm.investingtracker.dto.authentication.LoginIDsDTO;
import org.efrenjm.investingtracker.model.profile.Profile;
import org.efrenjm.investingtracker.service.authentication.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
	private final AuthenticationService authenticationService;

	@PostMapping("/login")
	public Mono<ResponseEntity<String>> login(@RequestBody LoginRequestDTO loginRequest) {
		return authenticationService.login(loginRequest.getEmail(), loginRequest.getPhone(), loginRequest.getPassword())
				.map(ResponseEntity::ok);
	}

	@PostMapping("/register")
	public Mono<ResponseEntity<String>> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
		return authenticationService.register(registerRequest)
				.map(user -> ResponseEntity.created(URI.create("/auth/login")).build());
	}

	@GetMapping("/generate-new-token")
	public Mono<ResponseEntity<String>> generateNewToken(@RequestParam LoginIDsDTO loginIds) {
		return authenticationService.generateNewVerificationToken(loginIds.getEmail(), loginIds.getPhone())
				.map(res -> ResponseEntity.ok("Token sent successfully"));
	}

	@GetMapping("/verify")
	public Mono<ResponseEntity<Profile>> verifyEmail(@RequestParam String token) {
		return authenticationService.verifyEmail(token)
				.map(ResponseEntity::ok);
	}
}

