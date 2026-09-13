package org.efrenjm.investingtracker.service.authentication;

import lombok.AllArgsConstructor;
import org.efrenjm.investingtracker.exception.authentication.*;
import org.efrenjm.investingtracker.model.organization.Organization;
import org.efrenjm.investingtracker.model.organization.UserRole;
import org.efrenjm.investingtracker.model.profile.Profile;
import org.efrenjm.investingtracker.service.utils.EmailService;
import org.efrenjm.investingtracker.service.utils.JwtService;
import org.efrenjm.investingtracker.dto.authentication.RegisterRequestDTO;
import org.efrenjm.investingtracker.model.auth_credentials.AuthCredentials;
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
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthenticationService implements IAuthenticationService {
	private final AuthCredentialsRepository authCredentialsRepository;
	private final ProfileRepository profileRepository;
	private final OrganizationRepository organizationRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final EmailService emailService;
	private final TransactionalOperator transactionalOperator;

	/**
	 * Authenticates a user using their email or phone number and password.
	 * <p>
	 * This method searches for authentication credentials based on the provided email or phone number.
	 * If a matching user is found, it verifies the password using a password encoder.
	 * If the password is valid, it generates and returns a JWT token.
	 * If authentication fails at any stage, it returns an InvalidCredentialsException.
	 *
	 * @param email The user's email address (can be null if phone is provided).
	 * @param phone The user's phone number (can be null if email is provided).
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
	 * Registers a new user in the system.
	 * <p>
	 * This method checks if the user already exists using their email or phone number.
	 * If neither is provided, it throws a MissingCredentialsException.
	 * If the user exists, it throws a UserAlreadyExistsException.
	 * Otherwise, it encrypts the password, generates a verification token, and
	 * creates a new authentication credentials record.
	 * The verification token is sent via email.
	 *
	 * @param user The registration request containing user details.
	 * @return A Mono containing the created AuthCredentials object.
	 * @throws MissingCredentialsException If no email or phone is provided.
	 * @throws UserAlreadyExistsException If the user already exists.
	 * @throws UserRegistrationException If an error occurs during registration.
	 */
	public Mono<AuthCredentials> register(RegisterRequestDTO user) {
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
			String verificationToken = generateVerificationToken();

			AuthCredentials newCredentials = AuthCredentials.builder()
					.email(email)
					.phoneNumber(phone)
					.password(password)
					.active(false)
					.verificationToken(verificationToken)
					.tokenExpiration(new Date(now.getTime() + 24 * 60 * 60 * 1000))
					.build();

			return authCredentialsRepository.save(newCredentials)
					.doOnSuccess(credentials -> emailService.sendVerificationEmail(email, verificationToken))
					.onErrorMap(e -> new UserRegistrationException("Error in user registration: " + e.getMessage()));
		});
	}

	/**
	 * Verifies a user's email using a verification token.
	 * <p>
	 * This method retrieves authentication credentials using the provided token.
	 * If the token is invalid or expired, it throws an exception.
	 * If the account is already verified, it throws an exception.
	 * Otherwise, it creates a new Profile and a default Organization for the user.
	 * It marks the credentials as verified and removes the verification token.
	 * The entire process runs within a transactional context.
	 *
	 * @param token The email verification token.
	 * @return A Mono containing the created Profile object.
	 * @throws InvalidTokenException If the token is invalid.
	 * @throws TokenExpiredException If the token has expired.
	 * @throws AccountAlreadyVerifiedException If the account is already verified.
	 * @throws UserRegistrationException If an error occurs during verification.
	 */
	@Transactional
	public Mono<Profile> verifyEmail(String token) {
		return authCredentialsRepository.findByVerificationToken(token)
				.switchIfEmpty(Mono.error(new InvalidTokenException()))
				.flatMap(credentials -> {
					Date now = new Date();

					if (credentials.getTokenExpiration().before(now)) {
						return Mono.error(new TokenExpiredException());
					} else if (credentials.isActive()) {
						return Mono.error(new AccountAlreadyVerifiedException());
					}

					Profile newProfile = Profile.builder()
							.createdAt(now)
							.updatedAt(now)
							.lastLogin(now)
							.email(credentials.getEmail())
							.phoneNumber(credentials.getPhoneNumber())
							.build();

					Organization newOrganization = Organization.builder()
							.name("Personal")
							.description("Personal organization")
							.createdAt(now)
							.updatedAt(now)
							.build();

					return profileRepository.save(newProfile)
							.flatMap(savedProfile -> {
								newOrganization.setCreatedBy(savedProfile);
								newOrganization.setUsers(List.of(new UserRole(savedProfile, "OWNER")));
								return organizationRepository.save(newOrganization)
										.flatMap(savedOrganization -> {
											newProfile.setOrganizations(List.of(savedOrganization));
											return profileRepository.save(savedProfile);
										});
							})
							.flatMap(profile -> {
								credentials.setActive(true);
								credentials.setVerificationToken(null);
								credentials.setTokenExpiration(null);
								return authCredentialsRepository.save(credentials)
										.thenReturn(profile);
							})
							.as(transactionalOperator::transactional)
							.onErrorMap(e -> new UserRegistrationException("Error verifying email: " + e.getMessage()));
				});
	}

	private String generateVerificationToken() {
		return UUID.randomUUID().toString();
	}
}
