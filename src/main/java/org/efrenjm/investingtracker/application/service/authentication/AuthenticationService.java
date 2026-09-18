package org.efrenjm.investingtracker.application.service.authentication;

import org.bson.types.ObjectId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.efrenjm.investingtracker.application.service.authentication.exceptions.*;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.model.account.DebitAccount;
import org.efrenjm.investingtracker.domain.model.user.CodeUsage;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.user.VerificationRequest;
import org.efrenjm.investingtracker.domain.model.user.exceptions.CodeExpiredException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.InvalidPasswordException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.NoVerificationInProcessException;
import org.efrenjm.investingtracker.domain.model.wallet.Visibility;
import org.efrenjm.investingtracker.domain.model.wallet.Wallet;
import org.efrenjm.investingtracker.domain.ports.inbound.*;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.AccountRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.UserRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.WalletRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.security.SessionPort;
import org.efrenjm.investingtracker.domain.service.AccountDomainService;
import org.efrenjm.investingtracker.domain.service.UserDomainService;
import org.efrenjm.investingtracker.domain.service.UserVerificationService;
import org.efrenjm.investingtracker.domain.service.WalletDomainService;
import org.efrenjm.investingtracker.infrastructure.logging.AppLogger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthPort
{
	private final UserRepositoryPort userRepository;
	private final WalletRepositoryPort walletRepository;
	private final AccountRepositoryPort accountRepository;

	private final UserDomainService userDomainService;
	private final WalletDomainService walletDomainService;
	private final AccountDomainService accountDomainService;

	private final ValidationPort validationService;
	private final UserVerificationService userVerificationService;
	private final EmailPort emailService;
	private final MessagePort messageService;
	private final SecurityPort securityService;
	private final SessionPort sessionService;
	private final TransactionalOperator transactionalOperator;

	@Override
	public Mono<User> login(String username, String password, ServerWebExchange exchange)
	{
		AppLogger.info(log, "AUTH-010", "login", "Attempting login for username: " + username);
		return userRepository.findByAnyCredential(username)
				.switchIfEmpty(Mono.defer(() -> {
					AppLogger.warn(log, "AUTH-011", "login", "Login failed: credential not found for " + username);
					return Mono.error(new InvalidCredentialsException());
				}))
				.flatMap(user -> {
					if (!securityService.arePasswordsEqual(password, user.getPassword()))
					{
						AppLogger.warn(log, "AUTH-012", "login", "Login failed: password mismatch for userId " + user.getId());
						return Mono.error(new InvalidCredentialsException());
					}

					if (!user.isEnabled())
					{
						if (user.isNewUser())
						{
							AppLogger.warn(log, "AUTH-013", "login", "Login failed: registration not completed for userId " + user.getId());
							return Mono.error(new RegistrationNotCompletedException());
						}
						AppLogger.warn(log, "AUTH-014", "login", "Login failed: user account inactive for userId " + user.getId());
						return Mono.error(new UserNotActiveException());
					}

					return securityService.generateToken(user)
							.flatMap(jwt -> securityService.setTokenInCookie(jwt, exchange.getResponse()))
							.doOnSuccess(v -> AppLogger.success(log, "AUTH-015", "login", "Login successful for userId: " + user.getId()))
							.thenReturn(user);
				});
	}

	@Override
	public Mono<Void> logout(UserIdentity user, ServerWebExchange exchange)
	{
		AppLogger.info(log, "AUTH-020", "logout", "Logging out userId: " + user.id());
		return sessionService.invalidateSession(user.id())
				.then(securityService.clearTokenCookie(exchange.getResponse()))
				.doOnSuccess(v -> AppLogger.success(log, "AUTH-021", "logout", "Logout successful for userId: " + user.id()));
	}

	@Override
	public Mono<User> register(String username, String password)
	{
		AppLogger.info(log, "AUTH-030", "register", "Registration attempt for: " + username);

		Mono<User> existingUserMono;
		if (validationService.isValidEmail(username))
		{
			existingUserMono = userRepository.findEmailInUse(username);
		}
		else if (validationService.isValidPhone(username))
		{
			existingUserMono = userRepository.findPhoneInUse(username);
		}
		else
		{
			AppLogger.warn(log, "AUTH-090", "register", "Invalid credential format: " + username);
			return Mono.error(new InvalidUsernameException(username));
		}

		return existingUserMono
				.flatMap(existingUser -> {
					if (!existingUser.isNewUser() || existingUser.isActive())
					{
						AppLogger.warn(log, "AUTH-032", "register", "Registration conflict detected: credential is already associated with an account");
						return Mono.just(createGenericRegistrationContext(username));
					}
					return prepareProvisionalPassword(existingUser, password)
							.flatMap(user -> handleUnverifiedUserRegistration(user, username));
				})
				.switchIfEmpty(Mono.defer(() -> {
					User newUser = userDomainService.createUser(username);
					return prepareProvisionalPassword(newUser, password)
							.flatMap(userRepository::save)
							.doOnSuccess(saved -> {
								AppLogger.success(log, "AUTH-033", "register", "User registered successfully with userId: " + saved.getId());
								this.sendVerificationRequest(saved);
							})
							.doOnError(e -> AppLogger.fail(log, "AUTH-034", "register", "Failed to save registered user for " + username, e));
				}));
	}

	private User createGenericRegistrationContext(String username)
	{
		CodeUsage codeUsage = validationService.isValidEmail(username)
				? CodeUsage.EMAIL_VERIFICATION
				: CodeUsage.PHONE_VERIFICATION;

		return User.builder()
				.id(new ObjectId().toString())
				.verificationRequest(VerificationRequest.builder()
						.codeUsage(codeUsage)
						.credential(username)
						.build())
				.build();
	}

	private Mono<User> prepareProvisionalPassword(User user, String password)
	{
		return Mono.just(userDomainService.resetUnverifiedPassword(user, password));
	}

	private Mono<User> handleUnverifiedUserRegistration(User existingUser, String credential)
	{
		VerificationRequest req = existingUser.getVerificationRequest().orElse(null);

		if (req == null)
		{
			CodeUsage codeUsage = validationService.isValidEmail(credential)
					? CodeUsage.EMAIL_VERIFICATION
					: CodeUsage.PHONE_VERIFICATION;
			existingUser.setVerificationRequest(userVerificationService.createRequest(codeUsage, credential));
			return userRepository.save(existingUser)
					.doOnSuccess(saved -> {
						AppLogger.success(log, "AUTH-035", "handleUnverifiedUserRegistration", "Re-initialized verification for unverified userId: " + saved.getId());
						this.sendVerificationRequest(saved);
					});
		}

		if (req.isRefreshable() || req.isExpired())
		{
			existingUser.setVerificationRequest(userVerificationService.refreshRequest(req));
			return userRepository.save(existingUser)
					.doOnSuccess(saved -> {
						AppLogger.success(log, "AUTH-036", "handleUnverifiedUserRegistration", "Refreshed code for unverified userId: " + saved.getId());
						this.sendVerificationRequest(saved);
					});
		}

		AppLogger.info(log, "AUTH-037", "handleUnverifiedUserRegistration", "Re-registration request for unverified userId " + existingUser.getId() + " within cooldown window");
		return userRepository.save(existingUser);
	}

	@Override
	public Mono<User> refreshVerificationCode(String userId)
	{
		AppLogger.info(log, "AUTH-040", "refreshVerificationCode", "Refreshing code for userId: " + userId);
		return userRepository.findById(userId)
				.flatMap(this::refreshVerificationCode)
				.doOnError(e -> AppLogger.fail(log, "AUTH-041", "refreshVerificationCode", "Failed to refresh code for userId: " + userId, e));
	}

	@Override
	public Mono<User> refreshVerificationCode(User user)
	{
		AppLogger.info(log, "AUTH-042", "refreshVerificationCode", "Refreshing code for user object userId: " + user.getId());
		VerificationRequest request = user.getVerificationRequest()
				.orElseThrow(() -> {
					AppLogger.warn(log, "AUTH-043", "refreshVerificationCode", "No verification in process for userId: " + user.getId());
					return new NoVerificationInProcessException();
				});

		user.setVerificationRequest(userVerificationService.refreshRequest(request));
		return userRepository.save(user)
				.doOnSuccess(saved -> {
					AppLogger.success(log, "AUTH-044", "refreshVerificationCode", "Verification code refreshed for userId: " + saved.getId());
					this.sendVerificationRequest(saved);
				});
	}

	@Override
	public Mono<User> verifyCode(String userId, String code)
	{
		AppLogger.info(log, "AUTH-050", "verifyCode", "Verifying code for userId: " + userId);
		return userRepository.findById(userId)
				.switchIfEmpty(Mono.error(new InvalidCodeException()))
				.flatMap(user -> verifyCode(user, code))
				.doOnError(e -> AppLogger.fail(log, "AUTH-051", "verifyCode", "Verification failed for userId: " + userId, e));
	}

	@Override
	public Mono<User> verifyCode(User user, String code)
	{
		AppLogger.info(log, "AUTH-052", "verifyCode", "Verifying code for user object userId: " + user.getId());
		VerificationRequest request = user.getVerificationRequest()
				.orElseThrow(() -> {
					AppLogger.warn(log, "AUTH-053", "verifyCode", "No verification request in progress for userId: " + user.getId());
					return new NoVerificationInProcessException();
				});
		try
		{
			userVerificationService.validateRequest(request, code);
		}
		catch (CodeExpiredException e)
		{
			AppLogger.warn(log, "AUTH-054", "verifyCode", "Code expired for userId: " + user.getId() + ". Refreshing code.");
			user.setVerificationRequest(userVerificationService.refreshRequest(request));
			return userRepository.save(user)
					.flatMap(savedUser -> {
						this.sendVerificationRequest(savedUser);
						return Mono.error(e);
					});
		}

		if (user.isNewUser())
		{
			AppLogger.info(log, "AUTH-055", "verifyCode", "Completing new user registration for userId: " + user.getId());
			return completeRegistration(user);
		}
		else
		{
			AppLogger.info(log, "AUTH-056", "verifyCode", "Completing credentials update for userId: " + user.getId());
			return completeCredentialsUpdate(user);
		}
	}

	@Override
	public Mono<User> updateCredential(User user, CodeUsage codeUsage, String credential)
	{
		AppLogger.info(log, "AUTH-060", "updateCredential", "Updating credential (" + codeUsage + ") for userId: " + user.getId());
		User updatedUser = userDomainService.updateCredential(user, codeUsage, credential);
		return userRepository.save(updatedUser)
				.doOnSuccess(saved -> {
					AppLogger.success(log, "AUTH-061", "updateCredential", "Credential update initiated for userId: " + saved.getId());
					this.sendVerificationRequest(saved);
				});
	}

	@Override
	public Mono<User> forgotPassword(String username, String newPassword)
	{
		AppLogger.info(log, "AUTH-070", "forgotPassword", "Forgot password request for username: " + username);
		return userRepository.findByAnyCredential(username)
				.switchIfEmpty(Mono.defer(() -> {
					AppLogger.warn(log, "AUTH-071", "forgotPassword", "Forgot password failed: user not found for " + username);
					return Mono.empty();
				}))
				.flatMap(user -> {
					if (user.isNewUser())
					{
						AppLogger.warn(log, "AUTH-072", "forgotPassword", "Forgot password failed: registration not completed for userId " + user.getId());
						return Mono.error(new RegistrationNotCompletedException());
					}
					return updateCredential(user, CodeUsage.PASSWORD_RESET, newPassword);
				});
	}

	@Override
	public Mono<User> updatePassword(User user, String newPassword, String oldPassword)
	{
		AppLogger.info(log, "AUTH-080", "updatePassword", "Updating password for userId: " + user.getId());
		User updatedUser = userDomainService.updatePassword(user, newPassword, oldPassword);
		return userRepository.save(updatedUser)
				.doOnSuccess(saved -> AppLogger.success(log, "AUTH-081", "updatePassword", "Password updated successfully for userId: " + saved.getId()));
	}



	private void sendVerificationRequest(User user)
	{
		VerificationRequest req = user.getVerificationRequest()
				.orElseThrow(() -> {
					AppLogger.fail(log, "AUTH-100", "sendVerificationRequest", "No verification request present for userId: " + user.getId());
					return new NoVerificationInProcessException();
				});

		AppLogger.info(log, "AUTH-101", "sendVerificationRequest", "Sending " + req.getCodeUsage() + " code to " + req.getCredential());
		switch (req.getCodeUsage())
		{
			case EMAIL_VERIFICATION -> emailService.sendVerificationEmail(req.getCredential(), req.getCode())
					.then()
					.doOnSuccess(v -> AppLogger.success(log, "AUTH-102", "sendVerificationRequest", "Verification email sent to " + req.getCredential()))
					.doOnError(e -> AppLogger.fail(log, "AUTH-103", "sendVerificationRequest", "Failed to send verification email to " + req.getCredential(), e))
					.subscribe();
			case PHONE_VERIFICATION -> messageService.sendVerificationMessage(req.getCredential(), req.getCode())
					.then()
					.doOnSuccess(v -> AppLogger.success(log, "AUTH-104", "sendVerificationRequest", "Verification SMS sent to " + req.getCredential()))
					.doOnError(e -> AppLogger.fail(log, "AUTH-105", "sendVerificationRequest", "Failed to send verification SMS to " + req.getCredential(), e))
					.subscribe();
			case PASSWORD_RESET -> {
				if (user.getEmail() != null)
				{
					emailService.sendVerificationEmail(user.getEmail(), req.getCode())
							.then()
							.doOnSuccess(v -> AppLogger.success(log, "AUTH-106", "sendVerificationRequest", "Password reset email sent to " + user.getEmail()))
							.doOnError(e -> AppLogger.fail(log, "AUTH-107", "sendVerificationRequest", "Failed to send password reset email to " + user.getEmail(), e))
							.subscribe();
				}
				else if (user.getPhoneNumber() != null)
				{
					messageService.sendVerificationMessage(user.getPhoneNumber(), req.getCode())
							.then()
							.doOnSuccess(v -> AppLogger.success(log, "AUTH-108", "sendVerificationRequest", "Password reset SMS sent to " + user.getPhoneNumber()))
							.doOnError(e -> AppLogger.fail(log, "AUTH-109", "sendVerificationRequest", "Failed to send password reset SMS to " + user.getPhoneNumber(), e))
							.subscribe();
				}
				else
				{
					AppLogger.fail(log, "AUTH-110", "sendVerificationRequest", "No credential available for password reset on userId: " + user.getId());
					throw new UnsupportedOperationException("No credential available for password reset");
				}
			}
		}
	}

	private Mono<User> completeRegistration(User user)
	{
		if (user.isActive())
		{
			user.clearVerificationRequest();
			AppLogger.warn(log, "AUTH-120", "completeRegistration", "Account already active for userId: " + user.getId());
			return userRepository.save(user)
					.flatMap(savedUser -> Mono.error(new AccountAlreadyVerifiedException()));
		}

		AppLogger.info(log, "AUTH-121", "completeRegistration", "Provisioning default wallet and debit account for userId: " + user.getId());
		Wallet newWallet = walletDomainService.createWallet(user.getId(), "Personal", "Personal wallet", Visibility.PRIVATE);
		DebitAccount newAccount = accountDomainService.createDebitAccount("Personal", "Personal account");
		newWallet.linkAccount(newAccount);

		return accountRepository.save(newAccount)
				.flatMap(savedAccount -> walletRepository.save(newWallet)
						.flatMap(savedWallet -> {
							user.linkWallet(savedWallet, "Owner");
							user.setActive(true);

							return completeCredentialsUpdate(user);
						})
				)
				.as(transactionalOperator::transactional)
				.doOnSuccess(u -> AppLogger.success(log, "AUTH-122", "completeRegistration", "Completed registration & activated userId: " + user.getId()))
				.doOnError(e -> AppLogger.fail(log, "AUTH-123", "completeRegistration", "Transaction failed while completing registration for userId: " + user.getId(), e))
				.thenReturn(user);
	}

	private Mono<User> completeCredentialsUpdate(User user)
	{
		userVerificationService.completeRequest(user);
		return userRepository.save(user)
				.doOnSuccess(saved -> AppLogger.success(log, "AUTH-130", "completeCredentialsUpdate", "Credentials updated and saved for userId: " + user.getId()))
				.thenReturn(user);
	}
}
