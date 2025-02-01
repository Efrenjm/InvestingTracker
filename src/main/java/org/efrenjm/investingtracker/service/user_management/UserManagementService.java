package org.efrenjm.investingtracker.service.user_management;

import org.efrenjm.investingtracker.config.JwtService;
import org.efrenjm.investingtracker.dto.authentication.RegisterRequestDTO;
import org.efrenjm.investingtracker.exception.authentication.InvalidCredentialsException;
import org.efrenjm.investingtracker.exception.authentication.UserAlreadyExistsException;
import org.efrenjm.investingtracker.model.Auth.Auth;
import org.efrenjm.investingtracker.model.Profile.Profile;
import org.efrenjm.investingtracker.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
public class UserManagementService implements IUserManagementService{
	@Autowired
	private AuthRepository authRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtService jwtService;

	public Mono<String> login(String loginId, String password) {
		return authRepository.findByEmailOrPhone(loginId, loginId)
				.filter(user -> passwordEncoder.matches(password, user.getPassword()))
				.flatMap(user -> jwtService.generateToken(user))
				.switchIfEmpty(Mono.error(new InvalidCredentialsException("Invalid credentials")));
	}

	public Mono<Auth> register(RegisterRequestDTO user) {
		String email = user.getEmail();
		String phone = user.getPhone();
		String password = passwordEncoder.encode(user.getPassword());

		return authRepository.existsByEmailOrPhone(email, phone)
				.flatMap(exists -> {
					if (exists)
						return Mono.error(new UserAlreadyExistsException("User already exists"));
//						return Mono.error(new RuntimeException("User already exists"));

					Auth newUser =  Auth.builder()
							.email(email)
							.phone(phone)
							.password(password)
							.build();

					Profile newProfile = Profile.builder()
							.auth(newUser)
							.build();

					return authRepository.save(newUser)
							.onErrorMap(e -> new RuntimeException("Error creating user"));
				});
	}
}
