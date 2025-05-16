package org.efrenjm.investingtracker.application.service.authentication;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.application.service.authentication.exceptions.*;
import org.efrenjm.investingtracker.application.service.security.SecurityService;
import org.efrenjm.investingtracker.application.service.utils.EmailService;
import org.efrenjm.investingtracker.domain.model.account.Account;
import org.efrenjm.investingtracker.domain.model.user.CodeUsage;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.user.VerificationRequest;
import org.efrenjm.investingtracker.domain.model.user.exceptions.CodeExpiredException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.NoVerificationInProcessException;
import org.efrenjm.investingtracker.domain.model.wallet.Wallet;
import org.efrenjm.investingtracker.domain.ports.inbound.AuthServicePort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.AccountRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.UserRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.WalletRepositoryPort;
import org.efrenjm.investingtracker.application.service.utils.ValidationService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthServicePort {
	private final UserRepositoryPort userRepository;
	private final WalletRepositoryPort walletRepository;
	private final AccountRepositoryPort accountRepository;

	private final ValidationService validationService;
	private final EmailService emailService;
	private final SecurityService securityService;
	private final TransactionalOperator transactionalOperator;
	private final ApplicationContext applicationContext;

	@Override
	public Mono<Void> login(String username, String password, ServerWebExchange exchange) {
		return userRepository.findByAnyCredential(username)
				.switchIfEmpty(Mono.error(new InvalidCredentialsException()))
				.flatMap(user -> {
					if (!securityService.arePasswordsEqual(password, user.getPassword())) {
						return Mono.error(new InvalidCredentialsException());
					}

					if (!user.isEnabled()) {
						if (user.isNewUser()) {
							return Mono.error(new RegistrationNotCompletedException());
						}
						return Mono.error(new UserNotActiveException());
					}

					return securityService.generateToken(user.getId())
							.flatMap(code -> securityService.setTokenInCookie(code, exchange.getResponse()));
				});
	}

	@Override
	public Mono<User> register(String username, String password) {
		return isValidRegistrationCredential(username)
				.flatMap(valid -> {
					if (valid == null || !valid) {
						return Mono.error(new UserAlreadyExistsException());
					}

					String encodedPassword = securityService.encode(password);
					Date now = new Date();
					
					User newUser = User.builder()
							.password(encodedPassword)
							.active(false)
							.createdAt(now)
							.updatedAt(now)
							.build();
							
					CodeUsage codeUsage = validationService.isValidEmail(username) ? CodeUsage.EMAIL_VERIFICATION : CodeUsage.PHONE_VERIFICATION;
					newUser.createVerificationRequest(codeUsage, username);
					return userRepository.save(newUser)
						.doOnSuccess(this::sendVerificationRequest);
				});
	}

	@Override
	public Mono<User> refreshVerificationCode(String userId) {
		return userRepository.findById(userId)
				.flatMap(this::refreshVerificationCode);
	}

	@Override
	public Mono<User> refreshVerificationCode(User user) {
		user.refreshVerificationRequest();
		return userRepository.save(user)
			.doOnSuccess(this::sendVerificationRequest);
	}

	@Override
//	@Transactional
	public Mono<User> verifyCode(String userId, String code) {
		return userRepository.findById(userId)
				.flatMap(user -> getSelf().verifyCode(user, code));
	}

	@Override
//	@Transactional
	public Mono<User> verifyCode(User user, String code) {
		try {
			user.validateCode(code);
		} catch (CodeExpiredException e) {
			user.refreshVerificationRequest();
			return userRepository.save(user)
					.flatMap(savedUser -> {
						this.sendVerificationRequest(savedUser);
						return Mono.error(e);
					});
		}
		if (user.isNewUser()) {
			return completeRegistration(user);
		} else {
			return completeCredentialsUpdate(user);
		}
	}

	@Override
	public Mono<User> updateCredential(User user, CodeUsage codeUsage, String credential) {
		switch (codeUsage) {
			case EMAIL_VERIFICATION:
				if (!validationService.isValidEmail(credential)) {
					return Mono.error(new InvalidEmailException(credential));
				}
				break;
			case PHONE_VERIFICATION:
				if (!validationService.isValidPhone(credential)) {
					return Mono.error(new InvalidPhoneNumberException(credential));
				}
				break;
			case PASSWORD_RESET:
				String encodedPassword = securityService.encode(credential);
				if (!validationService.isValidPassword(credential)) {
					return Mono.error(new InvalidPasswordException());
				}
				if (!user.getPassword().equals(encodedPassword)) {
					return Mono.error(new ReusedPasswordException());
				}
				credential = encodedPassword;
				break;
		}

		user.createVerificationRequest(codeUsage, credential);
		return userRepository.save(user)
				.doOnSuccess(this::sendVerificationRequest);
	}

	@Override
	public Mono<User> forgotPassword(String username, String newPassword) {
		return userRepository.findById(username)
				.flatMap(user -> {
					if (user.isNewUser()) {
						return Mono.error(new RegistrationNotCompletedException());
					}
					return updateCredential(user, CodeUsage.PASSWORD_RESET, newPassword);
				});
	}

	@Override
	public Mono<User> updatePassword(User user, String newPassword, String oldPassword) {
		if (!securityService.arePasswordsEqual(oldPassword, user.getPassword())) {
			return Mono.error(new InvalidOldPasswordException());
		}
		if (securityService.arePasswordsEqual(newPassword, user.getPassword())) {
			return Mono.error(new ReusedPasswordException());
		}
		user.setPassword(securityService.encode(newPassword));
		return userRepository.save(user);
	}

	private Mono<Boolean> isValidRegistrationCredential(String credential) {
		Mono<User> strategy;
		if (validationService.isValidEmail(credential)) {
			strategy = userRepository.findEmailInUse(credential);
		} else if (validationService.isValidPhone(credential)) {
			strategy = userRepository.findPhoneInUse(credential);
		} else {
			return Mono.error(new InvalidUsernameException(credential));
		}

		return strategy.flatMap(user -> {
					if (user.getVerificationRequest().isEmpty()) {
						return Mono.just(false);
					}
					VerificationRequest req = user.getVerificationRequest().get();

					if (req.getCredential().equals(credential) && req.getExpiration().before(new Date())) {
						if (user.isNewUser()) {
							return userRepository.delete(user.getId())
									.thenReturn(true);
						} else {
							user.clearVerificationRequest();
							return userRepository.save(user)
									.thenReturn(true);
						}
					}
					return Mono.just(false);
				})
				.defaultIfEmpty(true);
	}

	private void sendVerificationRequest(User user) {
		VerificationRequest req = user.getVerificationRequest()
				.orElseThrow(NoVerificationInProcessException::new);

		switch (req.getCodeUsage()) {
			case EMAIL_VERIFICATION -> emailService.sendVerificationEmail(req.getCredential(), req.getCode())
					.then().subscribe();
			case PHONE_VERIFICATION -> throw new UnsupportedOperationException("SMS sender not implemented yet");
			case PASSWORD_RESET -> {
				if (user.getEmail() != null) {
					throw new UnsupportedOperationException("Email template not implemented yet");
				} else {
					throw new UnsupportedOperationException("SMS sender not implemented yet");
				}
			}
		}
	}
	
	private Mono<User> completeRegistration(User user) {
		if (user.isActive()) {
			user.clearVerificationRequest();

			return userRepository.save(user)
					.flatMap(savedUser -> Mono.error(new AccountAlreadyVerifiedException()));
		}
		String accountId = new ObjectId().toString();
		String walletId = new ObjectId().toString();
		Wallet newWallet = Wallet.defaultWallet(walletId, user.getId(), accountId);
		Account newAccount = Account.defaultAccount(accountId, walletId);

		return accountRepository.save(newAccount)
				.flatMap(savedAccount -> walletRepository.save(newWallet)
						.flatMap(savedWallet -> {
							user.setWallets(List.of(savedWallet.getId()));
							user.setActive(true);

							return completeCredentialsUpdate(user);
						})
				)
				.as(transactionalOperator::transactional);
	}

	private Mono<User> completeCredentialsUpdate(User user) {
		user.completeVerificationRequest();
		return userRepository.save(user);
	}

	private AuthenticationService getSelf() {
		return applicationContext.getBean(AuthenticationService.class); // Obtener el bean dinámicamente
	}
}
