package org.efrenjm.investingtracker.application.service.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Date;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.application.security.port.out.SessionStorePort;
import org.efrenjm.investingtracker.application.service.authentication.exceptions.InvalidCredentialsException;
import org.efrenjm.investingtracker.application.service.authentication.exceptions.InvalidUsernameException;
import org.efrenjm.investingtracker.application.service.authentication.exceptions.RegistrationNotCompletedException;
import org.efrenjm.investingtracker.application.service.authentication.exceptions.UserNotActiveException;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.model.account.DebitAccount;
import org.efrenjm.investingtracker.domain.model.user.CodeUsage;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.user.VerificationRequest;
import org.efrenjm.investingtracker.domain.model.user.exceptions.CodeExpiredException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.InvalidCodeException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.InvalidPasswordException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.NoVerificationInProcessException;
import org.efrenjm.investingtracker.domain.model.wallet.Role;
import org.efrenjm.investingtracker.domain.model.wallet.Wallet;
import org.efrenjm.investingtracker.domain.ports.inbound.EmailPort;
import org.efrenjm.investingtracker.domain.ports.inbound.MessagePort;
import org.efrenjm.investingtracker.domain.ports.inbound.ValidationPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.AccountRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.UserRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.WalletRepositoryPort;
import org.efrenjm.investingtracker.domain.ports.outbound.security.JwtPort;
import org.efrenjm.investingtracker.domain.ports.outbound.security.PasswordEncoderPort;
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
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock private UserRepositoryPort userRepository;
    @Mock private WalletRepositoryPort walletRepository;
    @Mock private AccountRepositoryPort accountRepository;
    @Mock private UserDomainService userDomainService;
    @Mock private WalletDomainService walletDomainService;
    @Mock private AccountDomainService accountDomainService;
    @Mock private ValidationPort validationService;
    @Mock private UserVerificationService userVerificationService;
    @Mock private EmailPort emailService;
    @Mock private MessagePort messageService;
    @Mock private PasswordEncoderPort passwordEncoder;
    @Mock private JwtPort jwtPort;
    @Mock private SessionStorePort sessionStore;
    @Mock private TransactionalOperator transactionalOperator;
    @Mock private ServerWebExchange exchange;
    @Mock private ServerHttpResponse response;

    @InjectMocks private AuthenticationService authService;

    @BeforeEach
    void setUp() {
        lenient().when(exchange.getResponse()).thenReturn(response);
        lenient()
                .when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(i -> i.getArgument(0));
        lenient()
                .when(userDomainService.resetUnverifiedPassword(any(User.class), anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void loginValidCredentialsReturnsToken() {
        String username = "user@example.com";
        String password = "password123";
        String userId = new ObjectId().toString();
        String hashedPassword = "hashedPassword";
        String token = "jwt-token";

        User user =
                User.builder()
                        .id(userId)
                        .email(username)
                        .password(hashedPassword)
                        .active(true)
                        .build();

        when(userRepository.findByAnyCredential(username)).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(jwtPort.generateToken(any())).thenReturn(Mono.just(token));
        when(jwtPort.extractSessionId(token)).thenReturn("session-1");
        when(sessionStore.create(any(), any())).thenReturn(Mono.just(true));
        when(jwtPort.setTokenInCookie(eq(token), any())).thenReturn(Mono.empty());

        StepVerifier.create(authService.login(username, password, exchange))
                .expectNext(user)
                .verifyComplete();

        verify(jwtPort).generateToken(any());
        verify(jwtPort).setTokenInCookie(eq(token), any());
    }

    @Test
    void loginDoesNotIssueCookieWhenSessionStoreDoesNotCreateSession() {
        String username = "user@example.com";
        String password = "password123";
        String userId = new ObjectId().toString();
        String token = "jwt-token";
        User user =
                User.builder()
                        .id(userId)
                        .email(username)
                        .password("hashedPassword")
                        .active(true)
                        .build();

        when(userRepository.findByAnyCredential(username)).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(jwtPort.generateToken(any())).thenReturn(Mono.just(token));
        when(jwtPort.extractSessionId(token)).thenReturn("session-1");
        when(sessionStore.create(any(), any())).thenReturn(Mono.just(false));

        StepVerifier.create(authService.login(username, password, exchange)).expectError().verify();

        verify(jwtPort, never()).setTokenInCookie(anyString(), any());
    }

    @Test
    void logoutValidIdentityInvalidatesSessionAndClearsCookie() {
        String userId = new ObjectId().toString();
        UserIdentity userIdentity = new UserIdentity(userId, java.util.Set.of());

        when(exchange.getRequest())
                .thenReturn(
                        org.springframework.http.server.reactive.ServerHttpRequest.class.cast(
                                org.mockito.Mockito.mock(
                                        org.springframework.http.server.reactive.ServerHttpRequest
                                                .class)));
        when(exchange.getRequest().getCookies()).thenReturn(new LinkedMultiValueMap<>());
        when(jwtPort.clearTokenCookie(any())).thenReturn(Mono.empty());

        StepVerifier.create(authService.logout(userIdentity, exchange)).verifyComplete();

        verify(jwtPort).clearTokenCookie(eq(response));
    }

    @Test
    void logoutInvalidatesCurrentSessionIdentifierBeforeClearingCookie() {
        String userId = new ObjectId().toString();
        String sessionId = "session-123";
        UserIdentity userIdentity = new UserIdentity(userId, java.util.Set.of());

        LinkedMultiValueMap<String, HttpCookie> headers = new LinkedMultiValueMap<>();
        headers.add("jwt", new HttpCookie("jwt", "jwt-token"));
        when(exchange.getRequest())
                .thenReturn(
                        org.mockito.Mockito.mock(
                                org.springframework.http.server.reactive.ServerHttpRequest.class));
        when(exchange.getRequest().getCookies()).thenReturn(headers);
        when(jwtPort.extractSessionId("jwt-token")).thenReturn(sessionId);
        when(sessionStore.invalidate(sessionId)).thenReturn(Mono.just(true));
        when(jwtPort.clearTokenCookie(any())).thenReturn(Mono.empty());

        StepVerifier.create(authService.logout(userIdentity, exchange)).verifyComplete();

        verify(sessionStore).invalidate(sessionId);
        verify(jwtPort).clearTokenCookie(eq(response));
    }

    @Test
    void loginInvalidCredentialsThrowsException() {
        String username = "user@example.com";
        String password = "password123";
        String hashedPassword = "hashedPassword";

        User user = User.builder().password(hashedPassword).email(username).active(true).build();

        when(userRepository.findByAnyCredential(username)).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(false);

        StepVerifier.create(authService.login(username, password, exchange))
                .expectError(InvalidCredentialsException.class)
                .verify();
    }

    @Test
    void loginUserNotFoundThrowsException() {
        when(userRepository.findByAnyCredential("unknown@example.com")).thenReturn(Mono.empty());

        StepVerifier.create(authService.login("unknown@example.com", "password123", exchange))
                .expectError(InvalidCredentialsException.class)
                .verify();
    }

    @Test
    void loginUserNotEnabledNewUserThrowsRegistrationNotCompleted() {
        String username = "user@example.com";
        String password = "password123";
        String hashedPassword = "hashedPassword";

        // New user: no email and no phone set → isNewUser() returns true
        User user = User.builder().password(hashedPassword).active(false).build();

        when(userRepository.findByAnyCredential(username)).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);

        StepVerifier.create(authService.login(username, password, exchange))
                .expectError(RegistrationNotCompletedException.class)
                .verify();
    }

    @Test
    void loginUserNotEnabledExistingUserThrowsUserNotActive() {
        String username = "user@example.com";
        String password = "password123";
        String hashedPassword = "hashedPassword";

        User user = User.builder().password(hashedPassword).email(username).active(false).build();

        when(userRepository.findByAnyCredential(username)).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);

        StepVerifier.create(authService.login(username, password, exchange))
                .expectError(UserNotActiveException.class)
                .verify();
    }

    @Test
    void registerValidEmailCreatesUser() {
        String email = "user@example.com";
        String password = "password123";
        String encodedPassword = "encoded_password";

        VerificationRequest verificationRequest =
                VerificationRequest.builder()
                        .code("ABC123")
                        .codeUsage(CodeUsage.EMAIL_VERIFICATION)
                        .credential(email)
                        .expiration(new Date(System.currentTimeMillis() + 600_000))
                        .refreshPause(new Date(System.currentTimeMillis() + 60_000))
                        .build();

        User createdUser =
                User.builder()
                        .password(encodedPassword)
                        .active(false)
                        .verificationRequest(verificationRequest)
                        .build();

        when(validationService.isValidEmail(email)).thenReturn(true);
        when(userRepository.findEmailInUse(email)).thenReturn(Mono.empty());
        when(userDomainService.createUser(email)).thenReturn(createdUser);
        when(emailService.sendVerificationEmail(anyString(), anyString())).thenReturn(Mono.empty());

        when(userRepository.save(any(User.class)))
                .thenAnswer(
                        invocation -> {
                            User savedUser = invocation.getArgument(0);
                            savedUser.setId(new ObjectId().toString());
                            return Mono.just(savedUser);
                        });

        StepVerifier.create(authService.register(email, password))
                .assertNext(
                        user -> {
                            assert user.getPassword().equals(encodedPassword);
                            assert !user.isActive();
                            assert user.getVerificationRequest().isPresent();
                            assert user.getVerificationRequest().get().getCodeUsage()
                                    == CodeUsage.EMAIL_VERIFICATION;
                            assert user.getVerificationRequest()
                                    .get()
                                    .getCredential()
                                    .equals(email);
                        })
                .verifyComplete();

        verify(emailService).sendVerificationEmail(eq(email), anyString());
    }

    @Test
    void registerWithPasswordStoresEncodedPasswordBeforeSendingVerification() {
        String email = "new-user@example.com";
        String password = "Password1@";
        String encodedPassword = "encoded-password";
        User provisionalUser =
                User.builder()
                        .active(false)
                        .verificationRequest(
                                VerificationRequest.builder()
                                        .code("ABC123")
                                        .codeUsage(CodeUsage.EMAIL_VERIFICATION)
                                        .credential(email)
                                        .expiration(new Date(System.currentTimeMillis() + 600_000))
                                        .refreshPause(new Date(System.currentTimeMillis() + 60_000))
                                        .build())
                        .build();

        when(validationService.isValidEmail(email)).thenReturn(true);
        when(userRepository.findEmailInUse(email)).thenReturn(Mono.empty());
        when(userDomainService.createUser(email)).thenReturn(provisionalUser);
        when(userDomainService.resetUnverifiedPassword(provisionalUser, password))
                .thenAnswer(
                        invocation -> {
                            provisionalUser.setPassword(encodedPassword);
                            return provisionalUser;
                        });
        when(userRepository.save(provisionalUser)).thenReturn(Mono.just(provisionalUser));
        when(emailService.sendVerificationEmail(email, "ABC123")).thenReturn(Mono.empty());

        StepVerifier.create(authService.register(email, password))
                .assertNext(user -> assertEquals(encodedPassword, user.getPassword()))
                .verifyComplete();

        verify(userDomainService).resetUnverifiedPassword(provisionalUser, password);
        verify(emailService).sendVerificationEmail(email, "ABC123");
    }

    @Test
    void registerInvalidCredentialThrowsException() {
        String username = "not-an-email-or-phone";
        when(validationService.isValidEmail(username)).thenReturn(false);
        when(validationService.isValidPhone(username)).thenReturn(false);

        StepVerifier.create(authService.register(username, "Password1@"))
                .expectError(InvalidUsernameException.class)
                .verify();
    }

    @Test
    void registerWhenActiveUserExistsReturnsGenericRegistrationContext() {
        String email = "user@example.com";
        User existing = User.builder().id("u1").email(email).active(true).build();

        when(validationService.isValidEmail(email)).thenReturn(true);
        when(userRepository.findEmailInUse(email)).thenReturn(Mono.just(existing));

        StepVerifier.create(authService.register(email, "Password1@"))
                .assertNext(
                        user -> {
                            assertNotNull(user.getId());
                            assertNotEquals(existing.getId(), user.getId());
                            assertEquals(
                                    email,
                                    user.getVerificationRequest().orElseThrow().getCredential());
                        })
                .verifyComplete();

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(emailService);
    }

    @Test
    void registerWhenUnverifiedUserWithoutRequestShouldCreateAndSendVerification() {
        String email = "user@example.com";
        User existing = User.builder().id("u1").username(email).active(false).build();
        VerificationRequest request =
                VerificationRequest.builder()
                        .code("ABC123")
                        .codeUsage(CodeUsage.EMAIL_VERIFICATION)
                        .credential(email)
                        .expiration(new Date(System.currentTimeMillis() + 600_000))
                        .refreshPause(new Date(System.currentTimeMillis() - 60_000))
                        .build();

        when(validationService.isValidEmail(email)).thenReturn(true);
        when(userRepository.findEmailInUse(email)).thenReturn(Mono.just(existing));
        when(userVerificationService.createRequest(CodeUsage.EMAIL_VERIFICATION, email))
                .thenReturn(request);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(emailService.sendVerificationEmail(anyString(), anyString())).thenReturn(Mono.empty());

        StepVerifier.create(authService.register(email, "Password1@"))
                .assertNext(user -> assertTrue(user.getVerificationRequest().isPresent()))
                .verifyComplete();

        verify(emailService).sendVerificationEmail(eq(email), anyString());
    }

    @Test
    void registerWhenUnverifiedUserInCooldownShouldSaveWithoutSendingCode() {
        String email = "user@example.com";
        VerificationRequest requestInCooldown =
                VerificationRequest.builder()
                        .code("ABC123")
                        .codeUsage(CodeUsage.EMAIL_VERIFICATION)
                        .credential(email)
                        .expiration(new Date(System.currentTimeMillis() + 600_000))
                        .refreshPause(new Date(System.currentTimeMillis() + 60_000))
                        .build();
        User existing =
                User.builder()
                        .id("u1")
                        .username(email)
                        .active(false)
                        .verificationRequest(requestInCooldown)
                        .build();

        when(validationService.isValidEmail(email)).thenReturn(true);
        when(userRepository.findEmailInUse(email)).thenReturn(Mono.just(existing));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(authService.register(email, "Password1@"))
                .assertNext(user -> assertTrue(user.getVerificationRequest().isPresent()))
                .verifyComplete();

        verifyNoInteractions(emailService);
        verifyNoInteractions(messageService);
    }

    @Test
    void registerValidPhoneCreatesUserAndSendsSms() {
        String phone = "+5215551234567";
        User createdUser =
                User.builder()
                        .active(false)
                        .verificationRequest(
                                VerificationRequest.builder()
                                        .code("ABC123")
                                        .codeUsage(CodeUsage.PHONE_VERIFICATION)
                                        .credential(phone)
                                        .expiration(new Date(System.currentTimeMillis() + 600_000))
                                        .refreshPause(new Date(System.currentTimeMillis() + 60_000))
                                        .build())
                        .build();

        when(validationService.isValidEmail(phone)).thenReturn(false);
        when(validationService.isValidPhone(phone)).thenReturn(true);
        when(userRepository.findPhoneInUse(phone)).thenReturn(Mono.empty());
        when(userDomainService.createUser(phone)).thenReturn(createdUser);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(messageService.sendVerificationMessage(anyString(), anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(authService.register(phone, "Password1@"))
                .assertNext(user -> assertTrue(user.getVerificationRequest().isPresent()))
                .verifyComplete();

        verify(messageService).sendVerificationMessage(eq(phone), anyString());
    }

    @Test
    void refreshVerificationCodeExistingUserRefreshesCode() {
        String userId = new ObjectId().toString();
        String credential = "user@example.com";

        VerificationRequest originalRequest =
                VerificationRequest.builder()
                        .code("OLD123")
                        .codeUsage(CodeUsage.EMAIL_VERIFICATION)
                        .credential(credential)
                        .expiration(new Date(System.currentTimeMillis() + 600_000))
                        .refreshPause(new Date(System.currentTimeMillis() + 60_000))
                        .build();

        VerificationRequest refreshedRequest =
                VerificationRequest.builder()
                        .code("NEW456")
                        .codeUsage(CodeUsage.EMAIL_VERIFICATION)
                        .credential(credential)
                        .expiration(new Date(System.currentTimeMillis() + 600_000))
                        .refreshPause(new Date(System.currentTimeMillis() + 60_000))
                        .build();

        User user =
                User.builder()
                        .id(userId)
                        .active(false)
                        .verificationRequest(originalRequest)
                        .build();

        when(userVerificationService.refreshRequest(originalRequest)).thenReturn(refreshedRequest);
        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(emailService.sendVerificationEmail(anyString(), anyString())).thenReturn(Mono.empty());

        StepVerifier.create(authService.refreshVerificationCode(userId))
                .assertNext(
                        refreshedUser -> {
                            assert refreshedUser.getVerificationRequest().isPresent();
                        })
                .verifyComplete();

        verify(emailService).sendVerificationEmail(eq(credential), anyString());
    }

    @Test
    void refreshVerificationCodeWhenNoVerificationInProcessThrowsException() {
        User user = User.builder().id("u-1").build();

        assertThrows(
                NoVerificationInProcessException.class,
                () -> authService.refreshVerificationCode(user));
    }

    @Test
    void verifyCodeValidCodeCompletesRegistration() {
        String userId = new ObjectId().toString();
        String code = "ABC123";
        String walletId = new ObjectId().toString();
        String accountId = new ObjectId().toString();

        VerificationRequest request =
                VerificationRequest.builder()
                        .code(code)
                        .codeUsage(CodeUsage.EMAIL_VERIFICATION)
                        .credential("user@example.com")
                        .expiration(new Date(System.currentTimeMillis() + 600_000))
                        .refreshPause(new Date(System.currentTimeMillis() + 60_000))
                        .build();

        User user = User.builder().id(userId).active(false).verificationRequest(request).build();

        Wallet newWallet =
                Wallet.builder()
                        .id(walletId)
                        .name("Personal")
                        .roles(
                                new java.util.HashMap<>(
                                        java.util.Map.of(
                                                "Owner",
                                                        Role.builder()
                                                                .members(new java.util.HashSet<>())
                                                                .build(),
                                                "Manager",
                                                        Role.builder()
                                                                .members(new java.util.HashSet<>())
                                                                .build(),
                                                "Viewer",
                                                        Role.builder()
                                                                .members(new java.util.HashSet<>())
                                                                .build())))
                        .build();

        DebitAccount newAccount = DebitAccount.builder().id(accountId).name("Personal").build();

        doNothing().when(userVerificationService).validateRequest(request, code);
        doReturn(newWallet)
                .when(walletDomainService)
                .createWallet(anyString(), anyString(), anyString(), any());
        when(accountDomainService.createDebitAccount("Personal", "Personal account"))
                .thenReturn(newAccount);
        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(
                        inv -> {
                            Wallet w = inv.getArgument(0);
                            if (w.getId() == null) {
                                w.setId(walletId);
                            }
                            return Mono.just(w);
                        });
        when(accountRepository.save(any(DebitAccount.class)))
                .thenAnswer(
                        inv -> {
                            DebitAccount a = inv.getArgument(0);
                            if (a.getId() == null) {
                                a.setId(accountId);
                            }
                            return Mono.just(a);
                        });
        when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        doAnswer(
                        inv -> {
                            User u = inv.getArgument(0);
                            u.setActive(true);
                            u.setEmail("user@example.com");
                            u.setVerificationRequest(null);
                            return null;
                        })
                .when(userVerificationService)
                .completeRequest(any(User.class));

        StepVerifier.create(authService.verifyCode(userId, code))
                .assertNext(
                        verifiedUser -> {
                            assert verifiedUser.isActive();
                            assert verifiedUser.getWallets().isPresent();
                            assert verifiedUser.getWallets().get().size() == 1;
                            assert verifiedUser.getVerificationRequest().isEmpty();
                        })
                .verifyComplete();
    }

    @Test
    void verifyCodeWhenNoVerificationInProcessThrowsException() {
        User user = User.builder().id("u-1").build();

        assertThrows(
                NoVerificationInProcessException.class,
                () -> authService.verifyCode(user, "ABC123"));
    }

    @Test
    void verifyCodeWhenUserDoesNotExistThrowsInvalidCodeException() {
        String userId = new ObjectId().toString();

        when(userRepository.findById(userId)).thenReturn(Mono.empty());

        StepVerifier.create(authService.verifyCode(userId, "AAAAAA"))
                .expectError(InvalidCodeException.class)
                .verify();
    }

    @Test
    void verifyCodeExpiredCodeRefreshesAndThrows() {
        String userId = new ObjectId().toString();
        String code = "ABC123";
        String email = "user@example.com";

        VerificationRequest originalRequest =
                VerificationRequest.builder()
                        .code("OLD123")
                        .codeUsage(CodeUsage.EMAIL_VERIFICATION)
                        .credential(email)
                        .expiration(new Date(System.currentTimeMillis() - 1000))
                        .refreshPause(new Date(System.currentTimeMillis() - 1000))
                        .build();

        VerificationRequest refreshedRequest =
                VerificationRequest.builder()
                        .code("NEW456")
                        .codeUsage(CodeUsage.EMAIL_VERIFICATION)
                        .credential(email)
                        .expiration(new Date(System.currentTimeMillis() + 600_000))
                        .refreshPause(new Date(System.currentTimeMillis() + 60_000))
                        .build();

        User user =
                User.builder()
                        .id(userId)
                        .active(false)
                        .verificationRequest(originalRequest)
                        .build();

        doThrow(new CodeExpiredException())
                .when(userVerificationService)
                .validateRequest(originalRequest, code);
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
    void verifyCodeValidCodeForExistingUserCompletesCredentialUpdate() {
        String userId = "u-1";
        VerificationRequest request =
                VerificationRequest.builder()
                        .code("ABC123")
                        .codeUsage(CodeUsage.EMAIL_VERIFICATION)
                        .credential("new@example.com")
                        .expiration(new Date(System.currentTimeMillis() + 600_000))
                        .refreshPause(new Date(System.currentTimeMillis() - 1_000))
                        .build();
        User user =
                User.builder()
                        .id(userId)
                        .email("old@example.com")
                        .active(true)
                        .verificationRequest(request)
                        .build();

        doNothing().when(userVerificationService).validateRequest(request, "ABC123");
        doAnswer(
                        inv -> {
                            User u = inv.getArgument(0);
                            u.setEmail("new@example.com");
                            u.clearVerificationRequest();
                            return null;
                        })
                .when(userVerificationService)
                .completeRequest(any(User.class));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(authService.verifyCode(user, "ABC123"))
                .assertNext(
                        updated -> {
                            assertEquals("new@example.com", updated.getEmail());
                            assertTrue(updated.getVerificationRequest().isEmpty());
                        })
                .verifyComplete();
    }

    @Test
    void updateCredentialValidEmailCreatesVerificationRequest() {
        String userId = new ObjectId().toString();
        String newEmail = "new@example.com";

        VerificationRequest verificationRequest =
                VerificationRequest.builder()
                        .code("ABC123")
                        .codeUsage(CodeUsage.EMAIL_VERIFICATION)
                        .credential(newEmail)
                        .expiration(new Date(System.currentTimeMillis() + 600_000))
                        .refreshPause(new Date(System.currentTimeMillis() + 60_000))
                        .build();

        User user = User.builder().id(userId).active(true).email("old@example.com").build();

        User updatedUser =
                User.builder()
                        .id(userId)
                        .active(true)
                        .email("old@example.com")
                        .verificationRequest(verificationRequest)
                        .build();

        when(userDomainService.updateCredential(user, CodeUsage.EMAIL_VERIFICATION, newEmail))
                .thenReturn(updatedUser);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(emailService.sendVerificationEmail(anyString(), anyString())).thenReturn(Mono.empty());

        StepVerifier.create(
                        authService.updateCredential(user, CodeUsage.EMAIL_VERIFICATION, newEmail))
                .assertNext(
                        result -> {
                            assert result.getVerificationRequest().isPresent();
                            assert result.getVerificationRequest()
                                    .get()
                                    .getCredential()
                                    .equals(newEmail);
                            assert result.getVerificationRequest().get().getCodeUsage()
                                    == CodeUsage.EMAIL_VERIFICATION;
                        })
                .verifyComplete();

        verify(emailService).sendVerificationEmail(eq(newEmail), anyString());
    }

    @Test
    void forgotPasswordUserNotFoundCompletesEmpty() {
        when(userRepository.findByAnyCredential("unknown@example.com")).thenReturn(Mono.empty());

        StepVerifier.create(authService.forgotPassword("unknown@example.com", "NewPassword123!"))
                .verifyComplete();
    }

    @Test
    void forgotPasswordNewUserThrowsRegistrationNotCompleted() {
        User user = User.builder().id("u1").active(false).build();
        when(userRepository.findByAnyCredential("user@example.com")).thenReturn(Mono.just(user));

        StepVerifier.create(authService.forgotPassword("user@example.com", "NewPassword123!"))
                .expectError(RegistrationNotCompletedException.class)
                .verify();
    }

    @Test
    void forgotPasswordExistingUserTriggersPasswordResetFlow() {
        User user = User.builder().id("u1").email("user@example.com").active(true).build();
        User updated =
                User.builder()
                        .id("u1")
                        .email("user@example.com")
                        .active(true)
                        .verificationRequest(
                                VerificationRequest.builder()
                                        .code("ABC123")
                                        .codeUsage(CodeUsage.PASSWORD_RESET)
                                        .credential("encoded")
                                        .expiration(new Date(System.currentTimeMillis() + 600_000))
                                        .refreshPause(new Date(System.currentTimeMillis() + 60_000))
                                        .build())
                        .build();

        when(userRepository.findByAnyCredential("user@example.com")).thenReturn(Mono.just(user));
        when(userDomainService.updateCredential(user, CodeUsage.PASSWORD_RESET, "NewPassword123!"))
                .thenReturn(updated);
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(updated));
        when(emailService.sendVerificationEmail(eq("user@example.com"), anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(authService.forgotPassword("user@example.com", "NewPassword123!"))
                .assertNext(
                        result ->
                                assertEquals(
                                        CodeUsage.PASSWORD_RESET,
                                        result.getVerificationRequest()
                                                .orElseThrow()
                                                .getCodeUsage()))
                .verifyComplete();
    }

    @Test
    void updatePasswordValidPasswordUpdatesPassword() {
        String userId = new ObjectId().toString();
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String encodedNewPassword = "encoded_new_password";

        User user = User.builder().id(userId).password("encoded_old_password").active(true).build();

        User updatedUser =
                User.builder().id(userId).password(encodedNewPassword).active(true).build();

        when(userDomainService.updatePassword(user, newPassword, oldPassword))
                .thenReturn(updatedUser);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(authService.updatePassword(user, newPassword, oldPassword))
                .assertNext(
                        result -> {
                            assert result.getPassword().equals(encodedNewPassword);
                        })
                .verifyComplete();
    }

    @Test
    void updatePasswordIncorrectOldPasswordThrowsException() {
        String userId = new ObjectId().toString();
        String oldPassword = "wrongPassword";
        String newPassword = "newPassword";

        User user = User.builder().id(userId).password("encoded_password").active(true).build();

        when(userDomainService.updatePassword(user, newPassword, oldPassword))
                .thenThrow(new InvalidPasswordException());

        // The exception is thrown synchronously before a Mono is created
        assertThrows(
                InvalidPasswordException.class,
                () -> authService.updatePassword(user, newPassword, oldPassword));
    }
}
