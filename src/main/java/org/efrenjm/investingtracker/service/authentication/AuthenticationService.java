package org.efrenjm.investingtracker.service.authentication;

import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.exception.authentication.*;
import org.efrenjm.investingtracker.model.organization.account.Account;
import org.efrenjm.investingtracker.model.organization.Organization;
import org.efrenjm.investingtracker.model.organization.account.AccountType;
import org.efrenjm.investingtracker.model.organization.role.UserRole;
import org.efrenjm.investingtracker.model.user.CodeUsage;
import org.efrenjm.investingtracker.model.user.User;
import org.efrenjm.investingtracker.service.authentication.code_verification.CodeVerificationService;
import org.efrenjm.investingtracker.service.model.organization.OrganizationService;
import org.efrenjm.investingtracker.service.model.user.UserService;
import org.efrenjm.investingtracker.service.utils.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class AuthenticationService /*implements IAuthenticationService*/ {
	private final UserService userService;
	private final OrganizationService organizationService;
	private final CodeVerificationService codeVerificationService;
	
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final TransactionalOperator transactionalOperator;

	public Mono<Void> login(String email, String phone, String password, ServerWebExchange exchange) {
		return userService.fetchUserByEmailOrPhone(email, phone)
				.switchIfEmpty(Mono.error(new InvalidCredentialsException()))
				.flatMap(user -> {
					if (!passwordEncoder.matches(password, user.getPassword())) {
						return Mono.error(new InvalidCredentialsException());
					}

					if (!user.isActive()) {
						return Mono.error(new Error()); /*TODO: create AccountNotVerifiedException*/
					}

					return jwtService.generateToken(user)
							.flatMap(code -> jwtService.setTokenInCookie(code, exchange.getResponse()));
				});
	}

	public Mono<User> register(String email, String phone, String password) {
		String encodedPassword = passwordEncoder.encode(password);
		return userService.isEmailOrPhoneTaken(email, phone)
				.flatMap(exists -> {
					if (exists) {
						return Mono.error(new UserAlreadyExistsException());
					}

					Date now = new Date();
					CodeUsage codeUsage = email != null ? CodeUsage.EMAIL_VERIFICATION : CodeUsage.PHONE_VERIFICATION;
					User newUser = User.builder()
							.updateEmailRequest(email)
							.updatePhoneRequest(phone)
							.password(encodedPassword)
							.active(false)
							.createdAt(now)
							.updatedAt(now)
							.build();

					return codeVerificationService.createRequest(newUser, codeUsage)
							.onErrorMap(e -> new UserRegistrationException("Error in user registration: " + e.getMessage()));
				});
	}

	@Transactional
	public Mono<User> verifyCode(ObjectId userId, String code) {
		return userService.fetchUser(userId)
				.flatMap(user -> codeVerificationService.validate(user, code)
						.flatMap(isCodeValid -> {
							if (!isCodeValid) {
								return Mono.error(new InvalidCodeException());
							}

							if (user.isNewUser()) {
								return completeRegistration(user);
							} else {
								return completeCredentialsUpdate(user);
							}
						}));
	}

	public Mono<Boolean> generateNewVerificationCode(ObjectId userId, CodeUsage codeUsage) {
		return userService.fetchUser(userId)
				.flatMap(credentials -> {
					System.out.println(credentials);
					if (credentials.isActive()) {
						return Mono.error(new AccountAlreadyVerifiedException());
					}

					return codeVerificationService.createRequest(credentials, codeUsage)
							.thenReturn(true);
				})
				.onErrorMap(e -> new UserRegistrationException("Error generating new verification code: " + e.getMessage()));
	}

	public Mono<User> updateEmail(User user, String newEmail) {
		if (user.getUpdateEmailRequest() != null) {
			return Mono.error(new InvalidCodeException());
		}

		user.setUpdateEmailRequest(newEmail);
		user.setCodeUsage(CodeUsage.EMAIL_VERIFICATION);

		return codeVerificationService.createRequest(user, CodeUsage.EMAIL_VERIFICATION)
				.then(Mono.just(user));
	}

	public Mono<User> updatePhone(User user, String newPhone) {
		if (user.getUpdatePhoneRequest() != null) {
			return Mono.error(new InvalidCodeException());
		}

		user.setUpdatePhoneRequest(newPhone);
		user.setCodeUsage(CodeUsage.PHONE_VERIFICATION);

		return codeVerificationService.createRequest(user, CodeUsage.PHONE_VERIFICATION)
				.then(Mono.just(user));
	}

	public Mono<User> updatePassword(User user, String oldPassword, String newPassword) {
		if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
			return Mono.error(new InvalidCredentialsException());
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		return userService.saveUser(user);
	}

	private Mono<User> completeRegistration(User user) {
		if (user.isActive())
		{
			user.clearVerificationRequest();

			return userService.saveUser(user)
					.flatMap(savedUser -> Mono.error(new UserAlreadyExistsException())); // TODO: Change to user already verified
		}
		Date now = new Date();

		Account newAccount = Account.builder()
				.id(new ObjectId())
				.name("Personal")
				.description("Personal account")
				.type(AccountType.DEBIT)
				.available(0.0)
				.createdAt(now)
				.updatedAt(now)
				.build();

		Organization newOrganization = Organization.builder()
				.name("Personal")
				.description("Personal organization")
				.accounts(List.of(newAccount))
				.members(List.of(new UserRole(user.getId(), "OWNER")))
				.createdBy(user.getId())
				.createdAt(now)
				.updatedAt(now)
				.build();

		return organizationService.saveOrganization(newOrganization)
				.flatMap(savedOrganization -> {
					user.setOrganizations(List.of(savedOrganization.getId()));
					user.setActive(true);
					user.codeVerified();

					return userService.saveUser(user);
				})
				.as(transactionalOperator::transactional)
				.onErrorMap(e -> new UserRegistrationException("Error verifying email: " + e.getMessage()));
	}

	private Mono<User> completeCredentialsUpdate(User user) {
		user.codeVerified();

		return userService.saveUser(user)
				.onErrorMap(e -> new UserRegistrationException("Error verifying email: " + e.getMessage()));
				/* TODO: Change UserRegistrationException for the correct error */
	}
}
