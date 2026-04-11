package org.efrenjm.investingtracker.application.service.authentication;

import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

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
		return userRepository.findByAnyCredential(username)
				.switchIfEmpty(Mono.error(new InvalidCredentialsException()))
				.flatMap(user -> {
					if (!securityService.arePasswordsEqual(password, user.getPassword()))
					{
						return Mono.error(new InvalidCredentialsException());
					}

					if (!user.isEnabled())
					{
						if (user.isNewUser())
						{
							return Mono.error(new RegistrationNotCompletedException());
						}
						return Mono.error(new UserNotActiveException());
					}

					return securityService.generateToken(user)
							.flatMap(jwt -> securityService.setTokenInCookie(jwt, exchange.getResponse()))
							.thenReturn(user);
				});
	}

	@Override
	public Mono<Void> logout(UserIdentity user, ServerWebExchange exchange)
	{
		return sessionService.invalidateSession(user.id())
				.then(securityService.clearTokenCookie(exchange.getResponse()));
	}

	@Override
	public Mono<User> register(String username, String password)
	{
		if (!validationService.isValidPassword(password))
		{
			return Mono.error(new InvalidPasswordException());
		}
		return isValidRegistrationCredential(username)
				.flatMap(valid -> {
					if (valid == null || !valid)
					{
						return Mono.error(new UserAlreadyExistsException());
					}

					User newUser = userDomainService.createUser(username, password);
					return userRepository.save(newUser)
							.doOnSuccess(this::sendVerificationRequest);
				});
	}

	@Override
	public Mono<User> refreshVerificationCode(String userId)
	{
		return userRepository.findById(userId)
				.flatMap(this::refreshVerificationCode);
	}

	@Override
	public Mono<User> refreshVerificationCode(User user)
	{
		VerificationRequest request = user.getVerificationRequest()
				.orElseThrow(NoVerificationInProcessException::new);

		user.setVerificationRequest(userVerificationService.refreshRequest(request));
		return userRepository.save(user)
				.doOnSuccess(this::sendVerificationRequest);
	}

	@Override
	public Mono<User> verifyCode(String userId, String code)
	{
		return userRepository.findById(userId)
				.flatMap(user -> verifyCode(user, code));
	}

	@Override
	public Mono<User> verifyCode(User user, String code)
	{
		VerificationRequest request = user.getVerificationRequest()
				.orElseThrow(NoVerificationInProcessException::new);
		try
		{
			userVerificationService.validateRequest(request, code);
		}
		catch (CodeExpiredException e)
		{
			user.setVerificationRequest(userVerificationService.refreshRequest(request));
			return userRepository.save(user)
					.flatMap(savedUser -> {
						this.sendVerificationRequest(savedUser);
						return Mono.error(e);
					});
		}

		if (user.isNewUser())
		{
			return completeRegistration(user);
		}
		else
		{
			return completeCredentialsUpdate(user);
		}
	}

	@Override
	public Mono<User> updateCredential(User user, CodeUsage codeUsage, String credential)
	{
		User updatedUser = userDomainService.updateCredential(user, codeUsage, credential);
		return userRepository.save(updatedUser)
				.doOnSuccess(this::sendVerificationRequest);
	}

	@Override
	public Mono<User> forgotPassword(String username, String newPassword)
	{
		return userRepository.findByAnyCredential(username)
				.flatMap(user -> {
					if (user.isNewUser())
					{
						return Mono.error(new RegistrationNotCompletedException());
					}
					return updateCredential(user, CodeUsage.PASSWORD_RESET, newPassword);
				});
	}

	@Override
	public Mono<User> updatePassword(User user, String newPassword, String oldPassword)
	{
		User updatedUser = userDomainService.updatePassword(user, newPassword, oldPassword);
		return userRepository.save(updatedUser);
	}

	private Mono<Boolean> isValidRegistrationCredential(String credential)
	{
		Mono<User> strategy;
		if (validationService.isValidEmail(credential))
		{
			strategy = userRepository.findEmailInUse(credential);
		}
		else if (validationService.isValidPhone(credential))
		{
			strategy = userRepository.findPhoneInUse(credential);
		}
		else
		{
			return Mono.error(new InvalidUsernameException(credential));
		}

		return strategy
				.flatMap(user -> {
					if (user.getVerificationRequest().isEmpty())
					{
						return Mono.just(false);
					}
					VerificationRequest req = user.getVerificationRequest().get();

					if (req.getCredential().equals(credential) && req.isExpired())
					{
						if (user.isNewUser())
						{
							return userRepository.delete(user.getId())
									.thenReturn(true);
						}
						else
						{
							user.clearVerificationRequest();
							return userRepository.save(user)
									.thenReturn(true);
						}
					}
					return Mono.just(false);
				})
				.defaultIfEmpty(true);
	}

	private void sendVerificationRequest(User user)
	{
		VerificationRequest req = user.getVerificationRequest()
				.orElseThrow(NoVerificationInProcessException::new);

		switch (req.getCodeUsage())
		{
			case EMAIL_VERIFICATION -> emailService.sendVerificationEmail(req.getCredential(), req.getCode())
					.then()
					.subscribe();
			case PHONE_VERIFICATION -> messageService.sendVerificationMessage(req.getCredential(), req.getCode())
					.then()
					.subscribe();
			case PASSWORD_RESET -> {
				if (user.getEmail() != null)
				{
					emailService.sendVerificationEmail(user.getEmail(), req.getCode())
							.then()
							.subscribe();
				}
				else if (user.getPhoneNumber() != null)
				{
					messageService.sendVerificationMessage(user.getPhoneNumber(), req.getCode())
							.then()
							.subscribe();
				}
				else
				{
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

			return userRepository.save(user)
					.flatMap(savedUser -> Mono.error(new AccountAlreadyVerifiedException()));
		}

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
				.thenReturn(user);
	}

	private Mono<User> completeCredentialsUpdate(User user)
	{
		userVerificationService.completeRequest(user);
		return userRepository.save(user).thenReturn(user);
	}
}
