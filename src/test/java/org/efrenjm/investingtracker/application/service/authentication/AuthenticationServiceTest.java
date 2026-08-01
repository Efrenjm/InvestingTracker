package org.efrenjm.investingtracker.application.service.authentication;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.application.service.authentication.exceptions.InvalidCredentialsException;
import org.efrenjm.investingtracker.application.service.authentication.exceptions.RegistrationNotCompletedException;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.model.account.DebitAccount;
import org.efrenjm.investingtracker.domain.model.user.CodeUsage;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.user.VerificationRequest;
import org.efrenjm.investingtracker.domain.model.user.exceptions.CodeExpiredException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.InvalidPasswordException;
import org.efrenjm.investingtracker.domain.model.wallet.Role;
import org.efrenjm.investingtracker.domain.model.wallet.Wallet;
import org.efrenjm.investingtracker.domain.ports.inbound.EmailPort;
import org.efrenjm.investingtracker.domain.ports.inbound.MessagePort;
import org.efrenjm.investingtracker.domain.ports.inbound.SecurityPort;
import org.efrenjm.investingtracker.domain.ports.inbound.ValidationPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.AccountRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.UserRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.WalletRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.security.SessionPort;
import org.efrenjm.investingtracker.domain.service.AccountDomainService;
import org.efrenjm.investingtracker.domain.service.UserDomainService;
import org.efrenjm.investingtracker.domain.service.UserVerificationService;
import org.efrenjm.investingtracker.domain.service.WalletDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
	@Mock
	private UserRepositoryPort userRepository;
	@Mock
	private WalletRepositoryPort walletRepository;
	@Mock
	private AccountRepositoryPort accountRepository;
	@Mock
	private UserDomainService userDomainService;
	@Mock
	private WalletDomainService walletDomainService;
	@Mock
	private AccountDomainService accountDomainService;
	@Mock
	private ValidationPort validationService;
	@Mock
	private UserVerificationService userVerificationService;
	@Mock
	private EmailPort emailService;
	@Mock
	private MessagePort messageService;
	@Mock
	private SecurityPort securityService;
	@Mock
	private SessionPort sessionService;
	@Mock
	private TransactionalOperator transactionalOperator;
	@Mock
	private ServerWebExchange exchange;
	@Mock
	private ServerHttpResponse response;

	@InjectMocks
	private AuthenticationService authService;

	@BeforeEach
	void setUp() {
		lenient().when(exchange.getResponse()).thenReturn(response);
		lenient().when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(i -> i.getArgument(0));
	}

	@Test
	void login_ValidCredentials_ReturnsToken() {
		String username = "user@example.com";
		String password = "password123";
		String userId = new ObjectId().toString();
		String hashedPassword = "hashedPassword";
		String token = "jwt-token";

		User user = User.builder()
				.id(userId)
				.email(username)
				.password(hashedPassword)
				.active(true)
				.build();

		when(userRepository.findByAnyCredential(username)).thenReturn(Mono.just(user));
		when(securityService.arePasswordsEqual(password, hashedPassword)).thenReturn(true);
		when(securityService.generateToken(user)).thenReturn(Mono.just(token));
		when(securityService.setTokenInCookie(eq(token), any())).thenReturn(Mono.empty());

		StepVerifier.create(authService.login(username, password, exchange))
				.expectNext(user)
				.verifyComplete();

		verify(securityService).generateToken(user);
		verify(securityService).setTokenInCookie(eq(token), any());
	}

	@Test
	void logout_ValidIdentity_InvalidatesSessionAndClearsCookie() {
		String userId = new ObjectId().toString();
		UserIdentity userIdentity = new UserIdentity(userId, java.util.Set.of());

		when(sessionService.invalidateSession(userId)).thenReturn(Mono.just(true));
		when(securityService.clearTokenCookie(any())).thenReturn(Mono.empty());

		StepVerifier.create(authService.logout(userIdentity, exchange))
				.verifyComplete();

		verify(sessionService).invalidateSession(userId);
		verify(securityService).clearTokenCookie(eq(response));
	}

	@Test
	void login_InvalidCredentials_ThrowsException() {
		String username = "user@example.com";
		String password = "password123";
		String hashedPassword = "hashedPassword";

		User user = User.builder()
				.password(hashedPassword)
				.email(username)
				.active(true)
				.build();

		when(userRepository.findByAnyCredential(username)).thenReturn(Mono.just(user));
		when(securityService.arePasswordsEqual(password, hashedPassword)).thenReturn(false);

		StepVerifier.create(authService.login(username, password, exchange))
				.expectError(InvalidCredentialsException.class)
				.verify();
	}

	@Test
	void login_UserNotEnabled_NewUser_ThrowsRegistrationNotCompleted() {
		String username = "user@example.com";
		String password = "password123";
		String hashedPassword = "hashedPassword";

		// New user: no email and no phone set → isNewUser() returns true
		User user = User.builder()
				.password(hashedPassword)
				.active(false)
				.build();

		when(userRepository.findByAnyCredential(username)).thenReturn(Mono.just(user));
		when(securityService.arePasswordsEqual(password, hashedPassword)).thenReturn(true);

		StepVerifier.create(authService.login(username, password, exchange))
				.expectError(RegistrationNotCompletedException.class)
				.verify();
	}

	@Test
	void register_ValidEmail_CreatesUser() {
		String email = "user@example.com";
		String password = "password123";
		String encodedPassword = "encoded_password";

		VerificationRequest verificationRequest = VerificationRequest.builder()
				.code("ABC123")
				.codeUsage(CodeUsage.EMAIL_VERIFICATION)
				.credential(email)
				.expiration(new Date(System.currentTimeMillis() + 600_000))
				.refreshPause(new Date(System.currentTimeMillis() + 60_000))
				.build();

		User createdUser = User.builder()
				.password(encodedPassword)
				.active(false)
				.verificationRequest(verificationRequest)
				.build();

		when(validationService.isValidEmail(email)).thenReturn(true);
		when(userRepository.findEmailInUse(email)).thenReturn(Mono.empty());
		when(userDomainService.createUser(email)).thenReturn(createdUser);
		when(emailService.sendVerificationEmail(anyString(), anyString())).thenReturn(Mono.empty());

		when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
			User savedUser = invocation.getArgument(0);
			savedUser.setId(new ObjectId().toString());
			return Mono.just(savedUser);
		});

		StepVerifier.create(authService.register(email))
				.assertNext(user -> {
					assert user.getPassword().equals(encodedPassword);
					assert !user.isActive();
					assert user.getVerificationRequest().isPresent();
					assert user.getVerificationRequest().get().getCodeUsage() == CodeUsage.EMAIL_VERIFICATION;
					assert user.getVerificationRequest().get().getCredential().equals(email);
				})
				.verifyComplete();

		verify(emailService).sendVerificationEmail(eq(email), anyString());
	}

	@Test
	void refreshVerificationCode_ExistingUser_RefreshesCode() {
		String userId = new ObjectId().toString();
		String credential = "user@example.com";

		VerificationRequest originalRequest = VerificationRequest.builder()
				.code("OLD123")
				.codeUsage(CodeUsage.EMAIL_VERIFICATION)
				.credential(credential)
				.expiration(new Date(System.currentTimeMillis() + 600_000))
				.refreshPause(new Date(System.currentTimeMillis() + 60_000))
				.build();

		VerificationRequest refreshedRequest = VerificationRequest.builder()
				.code("NEW456")
				.codeUsage(CodeUsage.EMAIL_VERIFICATION)
				.credential(credential)
				.expiration(new Date(System.currentTimeMillis() + 600_000))
				.refreshPause(new Date(System.currentTimeMillis() + 60_000))
				.build();

		User user = User.builder()
				.id(userId)
				.active(false)
				.verificationRequest(originalRequest)
				.build();

		when(userVerificationService.refreshRequest(originalRequest)).thenReturn(refreshedRequest);
		when(userRepository.findById(userId)).thenReturn(Mono.just(user));
		when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
		when(emailService.sendVerificationEmail(anyString(), anyString())).thenReturn(Mono.empty());

		StepVerifier.create(authService.refreshVerificationCode(userId))
				.assertNext(refreshedUser -> {
					assert refreshedUser.getVerificationRequest().isPresent();
				})
				.verifyComplete();

		verify(emailService).sendVerificationEmail(eq(credential), anyString());
	}

	@Test
	void verifyCode_ValidCode_CompletesRegistration() {
		String userId = new ObjectId().toString();
		String code = "ABC123";
		String walletId = new ObjectId().toString();
		String accountId = new ObjectId().toString();

		VerificationRequest request = VerificationRequest.builder()
				.code(code)
				.codeUsage(CodeUsage.EMAIL_VERIFICATION)
				.credential("user@example.com")
				.expiration(new Date(System.currentTimeMillis() + 600_000))
				.refreshPause(new Date(System.currentTimeMillis() + 60_000))
				.build();

		User user = User.builder()
				.id(userId)
				.active(false)
				.verificationRequest(request)
				.build();

		Wallet newWallet = Wallet.builder()
				.id(walletId)
				.name("Personal")
				.roles(new java.util.HashMap<>(java.util.Map.of(
						"Owner", Role.builder().members(new java.util.HashSet<>()).build(),
						"Manager", Role.builder().members(new java.util.HashSet<>()).build(),
						"Viewer", Role.builder().members(new java.util.HashSet<>()).build()
				)))
				.build();

		DebitAccount newAccount = DebitAccount.builder()
				.id(accountId)
				.name("Personal")
				.build();

		doNothing().when(userVerificationService).validateRequest(request, code);
		doReturn(newWallet).when(walletDomainService).createWallet(anyString(), anyString(), anyString(), any());
		when(accountDomainService.createDebitAccount("Personal", "Personal account")).thenReturn(newAccount);
		when(userRepository.findById(userId)).thenReturn(Mono.just(user));
		when(walletRepository.save(any(Wallet.class))).thenAnswer(inv -> {
			Wallet w = inv.getArgument(0);
			if (w.getId() == null) w.setId(walletId);
			return Mono.just(w);
		});
		when(accountRepository.save(any(DebitAccount.class))).thenAnswer(inv -> {
			DebitAccount a = inv.getArgument(0);
			if (a.getId() == null) a.setId(accountId);
			return Mono.just(a);
		});
		when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
		doAnswer(inv -> {
			User u = inv.getArgument(0);
			u.setActive(true);
			u.setEmail("user@example.com");
			u.setVerificationRequest(null);
			return null;
		}).when(userVerificationService).completeRequest(any(User.class));

		StepVerifier.create(authService.verifyCode(userId, code))
				.assertNext(verifiedUser -> {
					assert verifiedUser.isActive();
					assert verifiedUser.getWallets().isPresent();
					assert verifiedUser.getWallets().get().size() == 1;
					assert verifiedUser.getVerificationRequest().isEmpty();
				})
				.verifyComplete();
	}

	@Test
	void verifyCode_ExpiredCode_RefreshesAndThrows() {
		String userId = new ObjectId().toString();
		String code = "ABC123";
		String email = "user@example.com";

		VerificationRequest originalRequest = VerificationRequest.builder()
				.code("OLD123")
				.codeUsage(CodeUsage.EMAIL_VERIFICATION)
				.credential(email)
				.expiration(new Date(System.currentTimeMillis() - 1000))
				.refreshPause(new Date(System.currentTimeMillis() - 1000))
				.build();

		VerificationRequest refreshedRequest = VerificationRequest.builder()
				.code("NEW456")
				.codeUsage(CodeUsage.EMAIL_VERIFICATION)
				.credential(email)
				.expiration(new Date(System.currentTimeMillis() + 600_000))
				.refreshPause(new Date(System.currentTimeMillis() + 60_000))
				.build();

		User user = User.builder()
				.id(userId)
				.active(false)
				.verificationRequest(originalRequest)
				.build();

		doThrow(new CodeExpiredException()).when(userVerificationService).validateRequest(originalRequest, code);
		when(userVerificationService.refreshRequest(originalRequest)).thenReturn(refreshedRequest);
		when(userRepository.findById(userId)).thenReturn(Mono.just(user));
		when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
		when(emailService.sendVerificationEmail(anyString(), anyString())).thenReturn(Mono.empty());

		StepVerifier.create(authService.verifyCode(userId, code))
				.expectError(CodeExpiredException.class)
				.verify();

		verify(emailService).sendVerificationEmail(eq(email), anyString());
	}

	@Test
	void updateCredential_ValidEmail_CreatesVerificationRequest() {
		String userId = new ObjectId().toString();
		String newEmail = "new@example.com";

		VerificationRequest verificationRequest = VerificationRequest.builder()
				.code("ABC123")
				.codeUsage(CodeUsage.EMAIL_VERIFICATION)
				.credential(newEmail)
				.expiration(new Date(System.currentTimeMillis() + 600_000))
				.refreshPause(new Date(System.currentTimeMillis() + 60_000))
				.build();

		User user = User.builder()
				.id(userId)
				.active(true)
				.email("old@example.com")
				.build();

		User updatedUser = User.builder()
				.id(userId)
				.active(true)
				.email("old@example.com")
				.verificationRequest(verificationRequest)
				.build();

		when(userDomainService.updateCredential(user, CodeUsage.EMAIL_VERIFICATION, newEmail)).thenReturn(updatedUser);
		when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
		when(emailService.sendVerificationEmail(anyString(), anyString())).thenReturn(Mono.empty());

		StepVerifier.create(authService.updateCredential(user, CodeUsage.EMAIL_VERIFICATION, newEmail))
				.assertNext(result -> {
					assert result.getVerificationRequest().isPresent();
					assert result.getVerificationRequest().get().getCredential().equals(newEmail);
					assert result.getVerificationRequest().get().getCodeUsage() == CodeUsage.EMAIL_VERIFICATION;
				})
				.verifyComplete();

		verify(emailService).sendVerificationEmail(eq(newEmail), anyString());
	}

	@Test
	void updatePassword_ValidPassword_UpdatesPassword() {
		String userId = new ObjectId().toString();
		String oldPassword = "oldPassword";
		String newPassword = "newPassword";
		String encodedNewPassword = "encoded_new_password";

		User user = User.builder()
				.id(userId)
				.password("encoded_old_password")
				.active(true)
				.build();

		User updatedUser = User.builder()
				.id(userId)
				.password(encodedNewPassword)
				.active(true)
				.build();

		when(userDomainService.updatePassword(user, newPassword, oldPassword)).thenReturn(updatedUser);
		when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

		StepVerifier.create(authService.updatePassword(user, newPassword, oldPassword))
				.assertNext(result -> {
					assert result.getPassword().equals(encodedNewPassword);
				})
				.verifyComplete();
	}

	@Test
	void updatePassword_IncorrectOldPassword_ThrowsException() {
		String userId = new ObjectId().toString();
		String oldPassword = "wrongPassword";
		String newPassword = "newPassword";

		User user = User.builder()
				.id(userId)
				.password("encoded_password")
				.active(true)
				.build();

		when(userDomainService.updatePassword(user, newPassword, oldPassword))
				.thenThrow(new InvalidPasswordException());

		// The exception is thrown synchronously before a Mono is created
		assertThrows(InvalidPasswordException.class,
				() -> authService.updatePassword(user, newPassword, oldPassword));
	}
}
