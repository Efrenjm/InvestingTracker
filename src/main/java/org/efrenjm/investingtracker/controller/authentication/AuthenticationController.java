package org.efrenjm.investingtracker.controller.authentication;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.config.AuthenticatedUser;
import org.efrenjm.investingtracker.dto.controller.authentication.*;
import org.efrenjm.investingtracker.model.user.CodeUsage;
import org.efrenjm.investingtracker.model.user.User;
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
	public Mono<ResponseEntity<String>> login(@RequestBody LoginRequestDTO req, ServerWebExchange exchange) {
		return authenticationService.login(req.getEmail(), req.getPhone(), req.getPassword(), exchange)
				.thenReturn(ResponseEntity.ok("Logged in successfully"));
	}

	@PostMapping("/register")
	public Mono<ResponseEntity<RegisterResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO req) {
		return authenticationService.register(req.getEmail(), req.getPhone(), req.getPassword())
				.map(user -> ResponseEntity.created(URI.create("/auth/login"))
						.body(RegisterResponseDTO.builder()
								.userId(user.getId().toString())
								.email(user.getUpdateEmailRequest())
								.phone(user.getUpdatePhoneRequest())
								.build()));
	}

	@GetMapping("/refresh-code/{codeUsage}")
	public Mono<ResponseEntity<String>> generateNewToken(@PathVariable String codeUsage, @RequestParam ObjectId userId) {
		CodeUsage parsedUsage = CodeUsage.fromValue(codeUsage);

		return authenticationService.generateNewVerificationCode(userId, parsedUsage)
				.map(res -> ResponseEntity.ok("Token sent successfully"));
	}

	@PostMapping("/verify-code")
	public Mono<ResponseEntity<VerifyCodeResponseDTO>> verifyCode(@RequestBody VerifyCodeRequestDTO req) {
		ObjectId userId = req.getUserId();
		String token = req.getToken();

		return authenticationService.verifyCode(userId, token)
				.map(savedUser -> ResponseEntity.created(URI.create("/user")) /*TODO: change create uri*/
						.body(VerifyCodeResponseDTO.builder()
								.userId(savedUser.getId().toString())
								.email(savedUser.getEmail())
								.phone(savedUser.getPhoneNumber())
								.build()));
	}

	@PutMapping("/email")
	public Mono<ResponseEntity<String>> updateEmail(@RequestBody String newEmail, @AuthenticatedUser User user) {
		return null;
	}

	@PutMapping("/phone")
	public Mono<ResponseEntity<String>> updatePhone(@RequestBody String newPhone, @AuthenticatedUser User user) {
		return null;
	}

	@PutMapping("/password")
	public Mono<ResponseEntity<String>> updatePassword(@Valid @RequestBody UpdatePasswordRequestDTO req, @AuthenticatedUser User user) {
		return authenticationService.updatePassword(user, req.getOldPassword(), req.getNewPassword())
				.map(res -> ResponseEntity.ok("Password updated successfully"));
	}
}

