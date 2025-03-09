package org.efrenjm.investingtracker.service.authentication;

import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.exception.authentication.*;
import org.efrenjm.investingtracker.model.account.Account;
import org.efrenjm.investingtracker.model.organization.Organization;
import org.efrenjm.investingtracker.model.organization.UserRole;
import org.efrenjm.investingtracker.model.profile.Profile;
import org.efrenjm.investingtracker.repository.AccountRepository;
import org.efrenjm.investingtracker.service.model.AuthCredentialsService;
import org.efrenjm.investingtracker.service.utils.EmailService;
import org.efrenjm.investingtracker.service.utils.JwtService;
import org.efrenjm.investingtracker.dto.authentication.RegisterRequestDTO;
import org.efrenjm.investingtracker.model.auth_credentials.AuthCredentials;
import org.efrenjm.investingtracker.repository.OrganizationRepository;
import org.efrenjm.investingtracker.repository.ProfileRepository;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class AuthenticationService implements IAuthenticationService {
	private static final int TOKEN_EXPIRATION = 10 * 60 * 1000;
	private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final int CODE_LENGTH = 6;

	private final AuthCredentialsService authCredentialsService;
	private final ProfileRepository profileRepository;
	private final OrganizationRepository organizationRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final EmailService emailService;
	private final TransactionalOperator transactionalOperator;
	private final AccountRepository accountRepository;

	public Mono<Void> login(String email, String phone, String password, ServerWebExchange exchange) {
		return authCredentialsService.fetchUserByEmailOrPhone(email, phone)
				.switchIfEmpty(Mono.error(new InvalidCredentialsException()))
				.flatMap(user -> {
					if (!passwordEncoder.matches(password, user.getPassword())) {
						return Mono.error(new InvalidCredentialsException());
					}
					return jwtService.generateToken(user)
							.flatMap(token -> setTokenInCookie(token, exchange.getResponse()));
				});
	}

	public Mono<AuthCredentials> register(RegisterRequestDTO user) {
		String email = user.getEmail();
		String phone = user.getPhone();
		String password = passwordEncoder.encode(user.getPassword());

		return authCredentialsService.existsUserByEmailOrPhone(email, phone)
				.flatMap(exists -> {
					if (exists) {
						return Mono.error(new UserAlreadyExistsException());
					}

					AuthCredentials newCredentials = AuthCredentials.builder()
							.email(email)
							.phoneNumber(phone)
							.password(password)
							.active(false)
							.build();

					return createTokenVerificationRequest(newCredentials)
							.onErrorMap(e -> new UserRegistrationException("Error in user registration: " + e.getMessage()));
				});
	}

	@Transactional
	public Mono<Profile> verifyToken(ObjectId userId, String token) {  // TODO: Change name to verifyToken
		return authCredentialsService.fetchUser(userId)
				.switchIfEmpty(Mono.error(new Error())) // TODO: Change to user not found
				.flatMap(user -> {
					if (!user.getVerificationToken().equals(token)) {
						return Mono.error(new InvalidTokenException());
					}

					return completeRegistration(user);
				});
	}

	public Mono<Boolean> generateNewVerificationToken(ObjectId userId) {
		return authCredentialsService.fetchUser(userId)
				.flatMap(credentials -> {
					if (credentials.isActive()) {
						return Mono.error(new AccountAlreadyVerifiedException());
					}

					return createTokenVerificationRequest(credentials)
							.thenReturn(true);
				})
				.onErrorMap(e -> new UserRegistrationException("Error generating new verification token: " + e.getMessage()));
	}

	private Mono<Profile> completeRegistration(AuthCredentials authCredentials) {
		Date now = new Date();

		if (authCredentials.getTokenExpiration().before(now)) {
			return createTokenVerificationRequest(authCredentials)
					.onErrorMap(e -> new UserRegistrationException("Error verifying email: " + e.getMessage()))
					.then(Mono.error(new TokenExpiredException()));
		} else if (authCredentials.isActive()) {
			return Mono.error(new AccountAlreadyVerifiedException());
		}

		Profile newProfile = Profile.builder()
				.createdAt(now)
				.updatedAt(now)
				.lastLogin(now)
				.email(authCredentials.getEmail())
				.phoneNumber(authCredentials.getPhoneNumber())
				.build();

		Organization newOrganization = Organization.builder()
				.name("Personal")
				.description("Personal organization")
				.createdAt(now)
				.updatedAt(now)
				.build();

		Account newAccount = Account.builder()
				.name("Personal")
				.description("Personal account")
				.type("DEBIT")
				.available(0.0)
				.createdAt(now)
				.updatedAt(now)
				.build();

		return profileRepository.save(newProfile)
				.flatMap(savedProfile -> accountRepository.save(newAccount)
						.flatMap(savedAccount -> {
							newOrganization.setCreatedBy(savedProfile);
							newOrganization.setMembers(List.of(new UserRole(savedProfile, "OWNER")));
							newOrganization.setAccounts(List.of(savedAccount));
							return organizationRepository.save(newOrganization)
									.flatMap(savedOrganization -> {
										savedProfile.setOrganizations(List.of(savedOrganization));
										savedAccount.setOrganization(savedOrganization);
										return accountRepository.save(savedAccount)
												.then(profileRepository.save(savedProfile));
									});
						})
				)
				.flatMap(savedProfile -> {
					authCredentials.setActive(true);
					authCredentials.setVerificationToken(null);
					authCredentials.setTokenExpiration(null);
					authCredentials.setProfile(savedProfile);
					return authCredentialsService.saveUser(authCredentials)
							.thenReturn(savedProfile);
				})
				.as(transactionalOperator::transactional)
				.onErrorMap(e -> new UserRegistrationException("Error verifying email: " + e.getMessage()));
	}

	private Mono<Void> setTokenInCookie(String token, ServerHttpResponse response) {
		ResponseCookie cookie = ResponseCookie.from("jwt", token)
				.httpOnly(true)
//				.secure(true)    // TODO: Implement HTTPS
				.path("/")
				.maxAge(24 * 60 * 60)
				.build();
		response.addCookie(cookie);
		return response.setComplete();
	}

	private Mono<AuthCredentials> createTokenVerificationRequest(AuthCredentials authCredentials) {
		Date now = new Date();
		String token = generateVerificationToken();

		authCredentials.setVerificationToken(token);
		authCredentials.setTokenExpiration(new Date(now.getTime() + TOKEN_EXPIRATION));

		return authCredentialsService.saveUser(authCredentials)
				.doOnSuccess(savedUser -> emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getVerificationToken()));
	}

	private static String generateVerificationToken() {
		Random random = new Random();
		StringBuilder code = new StringBuilder(CODE_LENGTH);

		for (int i = 0; i < CODE_LENGTH; i++) {
			int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
			char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
			code.append(randomChar);
		}

		return code.toString();
	}
}
