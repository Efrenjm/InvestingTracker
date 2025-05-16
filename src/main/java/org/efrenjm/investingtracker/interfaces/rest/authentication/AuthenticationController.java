package org.efrenjm.investingtracker.interfaces.rest.authentication;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.efrenjm.investingtracker.application.dto.controller.authentication.*;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.ports.inbound.AuthServicePort;
import org.efrenjm.investingtracker.interfaces.annotations.AuthenticatedUser;
import org.efrenjm.investingtracker.domain.exception.BadRequestException;
import org.efrenjm.investingtracker.domain.model.user.CodeUsage;
import org.efrenjm.investingtracker.infrastructure.security.SecurityUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
	private final AuthServicePort authenticationService;

	@PostMapping("/login")
	public Mono<ResponseEntity<Void>> login(@RequestBody LoginRequestDTO req, ServerWebExchange exchange) {
		return authenticationService.login(req.getUsername(), req.getPassword(), exchange)
				.thenReturn(ResponseEntity.ok().build());
	}

	@PostMapping("/register")
	public Mono<ResponseEntity<RegisterResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO req) {
		return authenticationService.register(req.getUsername(), req.getPassword())
				.map(user -> ResponseEntity.created(URI.create("/verify-code"))
						.body(new RegisterResponseDTO(user)));
	}

	@GetMapping("/refresh-code")
	public Mono<ResponseEntity<String>> refreshVerificationCode(@RequestParam(required = false) String userId,
	                                                            @AuthenticatedUser SecurityUser securityUser) {
				Mono<User> strategy;
				if (securityUser == null) {
					if (userId == null) {
						return Mono.error(new BadRequestException("If not authenticated, a User ID must be provided."));
					}
					strategy = authenticationService.refreshVerificationCode(userId);
				} else {
					strategy = authenticationService.refreshVerificationCode(securityUser.getDomainUser());
				}
				return strategy.map(res -> ResponseEntity.ok().build());
	}

	@PostMapping("/verify-code")
	public Mono<ResponseEntity<VerifyCodeResponseDTO>> verifyCode(@Valid @RequestBody VerifyCodeRequestDTO req,
	                                                              @AuthenticatedUser SecurityUser securityUser) {
		Mono<User> strategy;
		if (securityUser == null) {
			String userId = req.getUserId();
			if (userId == null) {
				return Mono.error(new BadRequestException("If not authenticated, a User ID must be provided."));
			}
			strategy = authenticationService.verifyCode(userId, req.getCode());
		} else {
			strategy = authenticationService.verifyCode(securityUser.getDomainUser(), req.getCode());
		}

		return strategy.map(savedUser -> ResponseEntity.ok().body(new VerifyCodeResponseDTO(savedUser)));
	}

	@PutMapping("/email")
	public Mono<ResponseEntity<String>> updateEmail(@RequestBody String newEmail, @AuthenticatedUser SecurityUser securityUser) {
		return authenticationService.updateCredential(securityUser.getDomainUser(), CodeUsage.EMAIL_VERIFICATION, newEmail)
				.map(res -> ResponseEntity.ok("Email updated successfully")); /* TODO: Change Response */
	}

	@PutMapping("/phone")
	public Mono<ResponseEntity<String>> updatePhone(@RequestBody String newPhone, @AuthenticatedUser SecurityUser securityUser) {
		return authenticationService.updateCredential(securityUser.getDomainUser(), CodeUsage.PHONE_VERIFICATION, newPhone)
				.map(res -> ResponseEntity.ok("Phone updated successfully")); /* TODO: Change Response */
	}

	@PutMapping("/password")
	public Mono<ResponseEntity<String>> updatePassword(@Valid @RequestBody UpdatePasswordRequestDTO req,
	                                                   @AuthenticatedUser SecurityUser securityUser) {
		return authenticationService.updatePassword(securityUser.getDomainUser(), req.getNewPassword(), req.getOldPassword())
				.map(res -> ResponseEntity.ok("Password updated successfully")); /* TODO: Change Response */
	}

	@PutMapping("/forgot-password")
	public Mono<ResponseEntity<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO req) {
		return authenticationService.forgotPassword(req.getUsername(), req.getNewPassword())
				.map(res -> ResponseEntity.ok("Password updated successfully")); /* TODO: Change Response */
	}
}
