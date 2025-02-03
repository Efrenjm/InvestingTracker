package org.efrenjm.investingtracker.service.authentication;

import lombok.AllArgsConstructor;
import org.efrenjm.investingtracker.config.JwtService;
import org.efrenjm.investingtracker.dto.authentication.RegisterRequestDTO;
import org.efrenjm.investingtracker.exception.authentication.InvalidCredentialsException;
import org.efrenjm.investingtracker.exception.authentication.MissingCredentialsException;
import org.efrenjm.investingtracker.exception.authentication.UserAlreadyExistsException;
import org.efrenjm.investingtracker.exception.authentication.UserRegistrationException;
import org.efrenjm.investingtracker.model.auth_credentials.AuthCredentials;
import org.efrenjm.investingtracker.model.organization.Organization;
import org.efrenjm.investingtracker.model.organization.UserRole;
import org.efrenjm.investingtracker.model.profile.Profile;
import org.efrenjm.investingtracker.repository.AuthCredentialsRepository;
import org.efrenjm.investingtracker.repository.OrganizationRepository;
import org.efrenjm.investingtracker.repository.ProfileRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class AuthenticationService implements IAuthenticationService {
	private final AuthCredentialsRepository authCredentialsRepository;
	private final ProfileRepository profileRepository;
	private final OrganizationRepository organizationRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final TransactionalOperator transactionalOperator;

	/**
	 * Authenticates a user by email or phone and password.
	 *
	 * @param email    The user's email.
	 * @param phone    The user's phone number.
	 * @param password The user's password.
	 * @return A Mono containing the JWT token if authentication is successful.
	 * @throws InvalidCredentialsException If the credentials are invalid.
	 */
	public Mono<String> login(String email, String phone, String password) {
		return authCredentialsRepository.findByEmailOrPhoneNumber(email, phone)
				.filter(user -> passwordEncoder.matches(password, user.getPassword()))
				.flatMap(jwtService::generateToken)
				.switchIfEmpty(Mono.error(new InvalidCredentialsException()));
	}

	/**
	 * Registers a new user with the provided details.
	 *
	 * @param user The registration request containing user details.
	 * @return A Mono containing the created Profile.
	 * @throws UserAlreadyExistsException If the user already exists.
	 * @throws MissingCredentialsException If neither email nor phone is provided.
	 * @throws UserRegistrationException If an error occurs during user creation.
	 */
	@Transactional
	public Mono<Profile> register(RegisterRequestDTO user) {
		Mono<Boolean> userExists;
		String email = user.getEmail();
		String phone = user.getPhone();

		if (email != null && phone != null) {
			userExists = authCredentialsRepository.existsByEmailOrPhoneNumber(email, phone);
		} else if (email != null) {
			userExists = authCredentialsRepository.existsByEmail(email);
		} else if (phone != null) {
			userExists = authCredentialsRepository.existsByPhoneNumber(phone);
		} else {
			return Mono.error(new MissingCredentialsException());
		}

		String password = passwordEncoder.encode(user.getPassword());

		return userExists.flatMap(exists -> {
			if (exists) {
				return Mono.error(new UserAlreadyExistsException());
			}

			Date now = new Date();
			Profile newProfile = Profile.builder()
					.createdAt(now)
					.updatedAt(now)
					.active(true)
					.email(email)
					.phoneNumber(phone)
					.lastLogin(now)
					.build();

			return profileRepository.save(newProfile)
					.flatMap(savedProfile -> {
						AuthCredentials newCredentials =  AuthCredentials.builder()
								.email(email)
								.phoneNumber(phone)
								.password(password)
								.profile(newProfile)
								.build();

						Organization newOrganization = Organization.builder()
								.name("Personal")
								.description("Personal organization")
								.createdBy(newProfile)
								.createdAt(now)
								.updatedAt(now)
								.users(List.of(new UserRole(newProfile, "OWNER")))
								.build();

						return transactionalOperator.transactional(
								authCredentialsRepository.save(newCredentials)
										.then(organizationRepository.save(newOrganization))
										.thenReturn(savedProfile)
						);
					})
					.onErrorMap(e ->  new UserRegistrationException("Error in user registration: " + e.getMessage()));
		});
	}
}
