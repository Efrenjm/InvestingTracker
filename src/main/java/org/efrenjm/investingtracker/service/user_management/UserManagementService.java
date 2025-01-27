package org.efrenjm.investingtracker.service.user_management;

import org.efrenjm.investingtracker.config.JwtService;
import org.efrenjm.investingtracker.model.Auth.Auth;
import org.efrenjm.investingtracker.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserManagementService {
	@Autowired
	private AuthRepository authRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtService jwtService;

	public Mono<String> login(String username, String password) {
		return authRepository.findByUsername(username)
				.filter(user -> passwordEncoder.matches(password, user.getPassword()))
				.flatMap(user -> jwtService.generateToken(user));
	}

	public Mono<Auth> register(String username, String password) {
		return authRepository.findByUsername(username)
				.handle((existingUser, sink) -> sink.error(new RuntimeException("User already exists")))
				.switchIfEmpty(Mono.defer(() -> {
					Auth auth = new Auth(username, passwordEncoder.encode(password));

					return authRepository.save(auth)
							.onErrorMap(e -> new RuntimeException("Error creating user"));
				}))
				.cast(Auth.class);
	}
}
