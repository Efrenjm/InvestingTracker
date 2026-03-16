package org.efrenjm.investingtracker.interfaces.rest.controller.authentication;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.ports.inbound.AuthPort;
import org.efrenjm.investingtracker.interfaces.annotations.AuthUser;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.RegisterResponseDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.UserPasswordDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.VerifyCodeRequestDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.VerifyCodeResponseDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.exception.NoUserProvidedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController
{
	private final AuthPort authenticationService;

	@PostMapping("/login")
	public Mono<ResponseEntity<Void>> login(@Valid @RequestBody UserPasswordDTO req, ServerWebExchange exchange)
	{
		return authenticationService.login(req.getUsername(), req.getPassword(), exchange)
				.thenReturn(ResponseEntity.ok().build());
	}

	@PostMapping("/logout")
	public Mono<ResponseEntity<Void>> logout(@Parameter(hidden = true) @AuthUser UserIdentity user,
	                                        @Parameter(hidden = true) ServerWebExchange exchange)
	{
		return authenticationService.logout(user, exchange)
				.thenReturn(ResponseEntity.noContent().build());
	}

	@PostMapping("/register")
	public Mono<ResponseEntity<RegisterResponseDTO>> register(@Valid @RequestBody UserPasswordDTO req)
	{
		return authenticationService.register(req.getUsername(), req.getPassword())
				.map(user -> ResponseEntity.created(URI.create("/verify-code"))
						.body(new RegisterResponseDTO(user)));
	}

	@GetMapping("/refresh-code")
	public Mono<ResponseEntity<String>> refreshVerificationCode(@RequestParam(required = false) String userId,
	                                                            @AuthUser User user)
	{
				Mono<User> strategy;
				if (user == null)
				{
					if (userId == null)
					{
						return Mono.error(new NoUserProvidedException());
					}
					strategy = authenticationService.refreshVerificationCode(userId);
				}
				else
				{
					strategy = authenticationService.refreshVerificationCode(user);
				}
				return strategy.map(res -> ResponseEntity.ok().build());
	}

	@PostMapping("/verify-code")
	public Mono<ResponseEntity<VerifyCodeResponseDTO>> verifyCode(@Valid @RequestBody VerifyCodeRequestDTO req,
	                                                              @AuthUser User user) {
		Mono<User> strategy;
		if (user == null)
		{
			String userId = req.getUserId();
			if (userId == null)
			{
				return Mono.error(new NoUserProvidedException());
			}
			strategy = authenticationService.verifyCode(userId, req.getCode());
		}
		else
		{
			strategy = authenticationService.verifyCode(user, req.getCode());
		}

		return strategy.map(savedUser -> ResponseEntity.ok().body(new VerifyCodeResponseDTO(savedUser)));
	}

//	@PutMapping("/email")
//	public Mono<ResponseEntity<String>> updateEmail(@RequestBody String newEmail, @AuthUser AuthenticatedUser user)
//	{
//		return authenticationService.updateCredential(user.getDomainUser(), CodeUsage.EMAIL_VERIFICATION, newEmail)
//				.map(res -> ResponseEntity.ok("Email updated successfully")); /* TODO: Change Response */
//	}
//
//	@PutMapping("/phone")
//	public Mono<ResponseEntity<String>> updatePhone(@RequestBody String newPhone, @AuthUser AuthenticatedUser user)
//	{
//		return authenticationService.updateCredential(user.getDomainUser(), CodeUsage.PHONE_VERIFICATION, newPhone)
//				.map(res -> ResponseEntity.ok("Phone updated successfully")); /* TODO: Change Response */
//	}
//
//	@PutMapping("/password")
//	public Mono<ResponseEntity<String>> updatePassword(@Valid @RequestBody UpdatePasswordRequestDTO req,
//	                                                   @AuthUser AuthenticatedUser user)
//	{
//		return authenticationService.updatePassword(user.getDomainUser(), req.getNewPassword(), req.getOldPassword())
//				.map(res -> ResponseEntity.ok("Password updated successfully")); /* TODO: Change Response */
//	}
//
//	@PostMapping("/forgot-password")
//	public Mono<ResponseEntity<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO req)
//	{
//		return authenticationService.forgotPassword(req.getUsername(), req.getNewPassword())
//				.map(res -> ResponseEntity.ok("Password updated successfully")); /* TODO: Change Response */
//	}
}
