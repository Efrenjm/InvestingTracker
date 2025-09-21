//package org.efrenjm.investingtracker.application.service.authentication;
//
//import org.bson.types.ObjectId;
//import org.efrenjm.investingtracker.application.service.authentication.exceptions.InvalidCredentialsException;
//import org.efrenjm.investingtracker.application.service.authentication.exceptions.InvalidOldPasswordException;
//import org.efrenjm.investingtracker.application.service.authentication.exceptions.RegistrationNotCompletedException;
//import org.efrenjm.investingtracker.application.service.security.SecurityService;
//import org.efrenjm.investingtracker.application.service.utils.EmailService;
//import org.efrenjm.investingtracker.domain.service.ValidationService;
//import org.efrenjm.investingtracker.domain.model.account.Account;
//import org.efrenjm.investingtracker.domain.model.user.CodeUsage;
//import org.efrenjm.investingtracker.domain.model.user.User;
//import org.efrenjm.investingtracker.domain.model.user.exceptions.CodeExpiredException;
//import org.efrenjm.investingtracker.domain.model.wallet.Wallet;
//import org.efrenjm.investingtracker.domain.ports.outbound.repository.AccountRepositoryPort;
//import org.efrenjm.investingtracker.domain.ports.outbound.repository.UserRepositoryPort;
//import org.efrenjm.investingtracker.domain.ports.outbound.repository.WalletRepositoryPort;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.context.ApplicationContext;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.transaction.reactive.TransactionalOperator;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//import static org.mockito.Mockito.verify;
//
//@ExtendWith(MockitoExtension.class)
//class AuthenticationServiceTest {
//	@Mock
//	private UserRepositoryPort userRepository;
//	@Mock
//	private WalletRepositoryPort walletRepository;
//	@Mock
//	private AccountRepositoryPort accountRepository;
//	@Mock
//	private ValidationService validationService;
//	@Mock
//	private EmailService emailService;
//	@Mock
//	private SecurityService securityService;
//	@Mock
//	private TransactionalOperator transactionalOperator;
//	@Mock
//	private ApplicationContext applicationContext;
//	@Mock
//	private ServerWebExchange exchange;
//	@Mock
//	private ServerHttpResponse response;
//
//	@InjectMocks
//	private AuthenticationService authService;
//
//	@BeforeEach
//	void setUp() {
//		lenient().when(applicationContext.getBean(AuthenticationService.class)).thenReturn(authService);
//		lenient().when(exchange.getResponse()).thenReturn(response);
//		lenient().when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(i -> i.getArgument(0));
//	}
//
//	@Test
//	void login_ValidCredentials_ReturnsToken() {
//		String username = "user@example.com";
//		String password = "password123";
//		String userId = new ObjectId().toString();
//		String hashedPassword = "hashedPassword";
//		String token = "jwt-token";
//
//		User user = User.builder()
//				.id(userId)
//				.password(hashedPassword)
//				.active(true)
//				.build();
//
//		when(userRepository.findByAnyCredential(username)).thenReturn(Mono.just(user));
//		when(securityService.arePasswordsEqual(password, hashedPassword)).thenReturn(true);
//		when(securityService.generateToken(userId)).thenReturn(Mono.just(token));
//		when(securityService.setTokenInCookie(eq(token), any())).thenReturn(Mono.empty());
//
//		StepVerifier.create(authService.login(username, password, exchange))
//				.verifyComplete();
//
//		verify(securityService).generateToken(userId);
//		verify(securityService).setTokenInCookie(eq(token), any());
//	}
//
//	@Test
//	void login_InvalidCredentials_ThrowsException() {
//		String username = "user@example.com";
//		String password = "password123";
//		String hashedPassword = "hashedPassword";
//
//		User user = User.builder()
//				.password(hashedPassword)
//				.active(true)
//				.build();
//
//		when(userRepository.findByAnyCredential(username)).thenReturn(Mono.just(user));
//		when(securityService.arePasswordsEqual(password, hashedPassword)).thenReturn(false);
//
//		StepVerifier.create(authService.login(username, password, exchange))
//				.expectError(InvalidCredentialsException.class)
//				.verify();
//	}
//
//	@Test
//	void login_UserNotEnabled_ThrowsException() {
//		String username = "user@example.com";
//		String password = "password123";
//		String hashedPassword = "hashedPassword";
//
//		User user = User.builder()
//				.password(hashedPassword)
//				.active(false)
//				.build();
//
//		when(userRepository.findByAnyCredential(username)).thenReturn(Mono.just(user));
//		when(securityService.arePasswordsEqual(password, hashedPassword)).thenReturn(true);
//
//		StepVerifier.create(authService.login(username, password, exchange))
//				.expectError(RegistrationNotCompletedException.class)
//				.verify();
//	}
//
//	@Test
//	void register_ValidEmail_CreatesUser() {
//		String email = "user@example.com";
//		String password = "password123";
//		String encodedPassword = "encoded_password";
//
//		when(validationService.isValidEmail(email)).thenReturn(true);
//		when(userRepository.findEmailInUse(email)).thenReturn(Mono.empty());
//		when(securityService.encode(password)).thenReturn(encodedPassword);
//		when(emailService.sendVerificationEmail(anyString(), anyString())).thenReturn(Mono.empty());
//
//		when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
//			User savedUser = invocation.getArgument(0);
//			savedUser.setId(new ObjectId().toString());
//			return Mono.just(savedUser);
//		});
//
//		StepVerifier.create(authService.register(email, password))
//				.assertNext(user -> {
//					assert user.getPassword().equals(encodedPassword);
//					assert !user.isActive();
//					assert user.getVerificationRequest().isPresent();
//					assert user.getVerificationRequest().get().getCodeUsage() == CodeUsage.EMAIL_VERIFICATION;
//					assert user.getVerificationRequest().get().getCredential().equals(email);
//				})
//				.verifyComplete();
//
//		verify(emailService).sendVerificationEmail(eq(email), anyString());
//	}
//
//	@Test
//	void refreshVerificationCode_ExistingUser_RefreshesCode() {
//		String userId = new ObjectId().toString();
//		User user = spy(User.builder()
//				.id(userId)
//				.active(false)
//				.build());
//
//		CodeUsage codeUsage = CodeUsage.EMAIL_VERIFICATION;
//		String credential = "user@example.com";
//		user.createVerificationRequest(codeUsage, credential);
//		doNothing().when(user).refreshVerificationRequest();
//
//		when(userRepository.findById(userId)).thenReturn(Mono.just(user));
//		when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
//		when(emailService.sendVerificationEmail(anyString(), anyString())).thenReturn(Mono.empty());
//
//		StepVerifier.create(authService.refreshVerificationCode(userId))
//				.assertNext(refreshedUser -> {
//					assert refreshedUser.getVerificationRequest().isPresent();
//				})
//				.verifyComplete();
//
//		verify(emailService).sendVerificationEmail(eq(credential), anyString());
//	}
//
//	@Test
//	void verifyCode_ValidCode_CompletesRegistration() {
//		String userId = new ObjectId().toString();
//		String code = "ABC123";
//		String walletId = new ObjectId().toString();
//		String accountId = new ObjectId().toString();
//
//		User user = spy(User.builder()
//				.id(userId)
//				.active(false)
//				.build());
//
//		user.createVerificationRequest(CodeUsage.EMAIL_VERIFICATION, "user@example.com");
//
//		when(user.isNewUser()).thenReturn(true);
//		doNothing().when(user).validateCode(code);
//
//		when(userRepository.findById(userId)).thenReturn(Mono.just(user));
//		when(walletRepository.save(any(Wallet.class))).thenReturn(
//				Mono.just(Wallet.defaultWallet(walletId, userId, accountId)));
//		when(accountRepository.save(any(Account.class))).thenReturn(
//				Mono.just(Account.defaultAccount(accountId, walletId)));
//		when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
//
//		StepVerifier.create(authService.verifyCode(userId, code))
//				.assertNext(verifiedUser -> {
//					assert verifiedUser.isActive();
//					assert verifiedUser.getWallets().size() == 1;
//					assert verifiedUser.getVerificationRequest().isEmpty();
//				})
//				.verifyComplete();
//	}
//
//	@Test
//	void verifyCode_ExpiredCode_RefreshesCode() {
//		String userId = new ObjectId().toString();
//		String code = "ABC123";
//		String email = "user@example.com";
//
//		User user = spy(User.builder()
//				.id(userId)
//				.active(false)
//				.build());
//
//		user.createVerificationRequest(CodeUsage.EMAIL_VERIFICATION, email);
//
//		doThrow(new CodeExpiredException()).when(user).validateCode(code);
//		doNothing().when(user).refreshVerificationRequest();
//
//		when(userRepository.findById(userId)).thenReturn(Mono.just(user));
//		when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
//		when(emailService.sendVerificationEmail(anyString(), anyString())).thenReturn(Mono.empty());
//
//		StepVerifier.create(authService.verifyCode(userId, code))
//				.expectError(CodeExpiredException.class)
//				.verify();
//
//		verify(emailService).sendVerificationEmail(eq(email), anyString());
//	}
//
//	@Test
//	void updateCredential_ValidEmail_CreatesVerificationRequest() {
//		String userId = new ObjectId().toString();
//		String newEmail = "new@example.com";
//
//		User user = User.builder()
//				.id(userId)
//				.active(true)
//				.email("old@example.com")
//				.build();
//
//		when(validationService.isValidEmail(newEmail)).thenReturn(true);
//		when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
//		when(emailService.sendVerificationEmail(anyString(), anyString())).thenReturn(Mono.empty());
//
//		StepVerifier.create(authService.updateCredential(user, CodeUsage.EMAIL_VERIFICATION, newEmail))
//				.assertNext(updatedUser -> {
//					assert updatedUser.getVerificationRequest().isPresent();
//					assert updatedUser.getVerificationRequest().get().getCredential().equals(newEmail);
//					assert updatedUser.getVerificationRequest().get().getCodeUsage() == CodeUsage.EMAIL_VERIFICATION;
//				})
//				.verifyComplete();
//
//		verify(emailService).sendVerificationEmail(eq(newEmail), anyString());
//	}
//
//	@Test
//	void updatePassword_ValidPassword_UpdatesPassword() {
//		String userId = new ObjectId().toString();
//		String oldPassword = "oldPassword";
//		String newPassword = "newPassword";
//		String encodedNewPassword = "encoded_new_password";
//
//		User user = User.builder()
//				.id(userId)
//				.password("encoded_old_password")
//				.active(true)
//				.build();
//
//		when(securityService.arePasswordsEqual(oldPassword, user.getPassword())).thenReturn(true);
//		when(securityService.arePasswordsEqual(newPassword, user.getPassword())).thenReturn(false);
//		when(securityService.encode(newPassword)).thenReturn(encodedNewPassword);
//		when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
//
//		StepVerifier.create(authService.updatePassword(user, newPassword, oldPassword))
//				.assertNext(updatedUser -> {
//					assert updatedUser.getPassword().equals(encodedNewPassword);
//				})
//				.verifyComplete();
//	}
//
//	@Test
//	void updatePassword_IncorrectOldPassword_ThrowsException() {
//		String userId = new ObjectId().toString();
//		String oldPassword = "wrongPassword";
//		String newPassword = "newPassword";
//
//		User user = User.builder()
//				.id(userId)
//				.password("encoded_password")
//				.active(true)
//				.build();
//
//		when(securityService.arePasswordsEqual(oldPassword, user.getPassword())).thenReturn(false);
//
//		StepVerifier.create(authService.updatePassword(user, newPassword, oldPassword))
//				.expectError(InvalidOldPasswordException.class)
//				.verify();
//	}
//}
