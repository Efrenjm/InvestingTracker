package org.efrenjm.investingtracker.controller.authentication;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.dto.authentication.*;
import org.efrenjm.investingtracker.service.authentication.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
	private final AuthenticationService authenticationService;

	@PostMapping("/login")
	public Mono<ResponseEntity<String>> login(@RequestBody LoginRequestDTO loginRequest, ServerWebExchange exchange) {
		return authenticationService.login(loginRequest.getEmail(), loginRequest.getPhone(), loginRequest.getPassword(), exchange)
				.thenReturn(ResponseEntity.ok("Logged in successfully"));
	}

	@PostMapping("/register")
	public Mono<ResponseEntity<RegisterResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
		return authenticationService.register(registerRequest)
				.map(user -> ResponseEntity.created(URI.create("/auth/login"))
						.body(RegisterResponseDTO.builder()
								.userId(user.getId().toString())
								.email(user.getEmail())
								.phone(user.getPhoneNumber())
								.build()));
	}

	@GetMapping("/generate-new-token")
	public Mono<ResponseEntity<String>> generateNewToken(@RequestParam ObjectId userId) {
		return authenticationService.generateNewVerificationToken(userId)
				.map(res -> ResponseEntity.ok("Token sent successfully"));
	}

	@PostMapping("/verifyToken")
	public Mono<ResponseEntity<CompleteRegistrationResponseDTO>> completeRegistration(@RequestBody CompleteRegistrationRequestDTO completeRegistrationRequest) {
		return authenticationService.verifyToken(completeRegistrationRequest.getUserId(), completeRegistrationRequest.getToken())
				.map(profile -> ResponseEntity.created(URI.create("/profile/" + profile.getId()))
						.body(CompleteRegistrationResponseDTO.builder()
								.profileId(profile.getId().toString())
								.email(profile.getEmail())
								.phone(profile.getPhoneNumber())
								.build()));
	}
}

