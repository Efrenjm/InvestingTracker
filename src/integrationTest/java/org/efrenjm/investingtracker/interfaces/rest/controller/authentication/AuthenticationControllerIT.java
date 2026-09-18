package org.efrenjm.investingtracker.interfaces.rest.controller.authentication;

import org.efrenjm.investingtracker.integration.IntegrationTestBase;
import org.efrenjm.investingtracker.integration.MailpitClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the /auth endpoint.
 *
 * Covered flow:
 *   1. POST /auth/register     -> creates user and sends verification email
 *   2. MailpitClient           -> reads the email and extracts the code
 *   3. POST /auth/verify-code  -> activates the account with the code
 *   4. POST /auth/login        -> returns JWT in cookie
 *   5. GET  /auth/refresh-code -> enforces cooldown before re-sending code
 */
@DisplayName("Authentication Controller - Integration Tests")
class AuthenticationControllerIT extends IntegrationTestBase {

    @Autowired
    private WebTestClient webTestClient;

    private MailpitClient mailpit;

    private static final String PASSWORD = "Password1@";
    private static final String BASE     = "/auth";

    @BeforeEach
    void setUp() throws Exception {
        mailpit = new MailpitClient(getMailpitApiUrl());
        // Clean previous emails (MongoDB/Redis are already cleaned in cleanDatabase())
        mailpit.deleteAllMessages();
    }

    // Generates a unique email per test to avoid collisions
    private String uniqueEmail() {
        return "test-" + java.util.UUID.randomUUID() + "@example.com";
    }

    // -------------------------------------------------------------------------
    // Register
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("register - valid credentials return 201 with userId")
    void register_ValidCredentials_Returns201() {
        String email = uniqueEmail();
        webTestClient.post()
                .uri(BASE + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"username": "%s", "password": "%s", "confirmPassword": "%s"}
                        """.formatted(email, PASSWORD, PASSWORD))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.userId").isNotEmpty()
                .jsonPath("$.username").isEqualTo(email);
    }

    @Test
    @DisplayName("register - active duplicate email returns generic success")
    void register_DuplicateEmail_ReturnsGenericSuccess() {
        String email = uniqueEmail();
        // First time: register and verify
        registerAndVerify(email);

        // Second time with the same email
        webTestClient.post()
                .uri(BASE + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"username": "%s", "password": "%s", "confirmPassword": "%s"}
                        """.formatted(email, PASSWORD, PASSWORD))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.message").isEqualTo("You’re almost there! Check your inbox for the next steps.");
    }

    @Test
    @DisplayName("register - invalid password returns 400")
    void register_InvalidPassword_Returns400() {
        webTestClient.post()
                .uri(BASE + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"username": "%s", "password": "weak", "confirmPassword": "weak"}
                        """.formatted(uniqueEmail()))
                .exchange()
                .expectStatus().isBadRequest();
    }

    // -------------------------------------------------------------------------
    // Verify-code
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("verify-code - valid code activates account without creating a session")
    void verifyCode_ValidCode_Returns200() throws Exception {
        String email = uniqueEmail();
        // 1. Register
        String userId = doRegister(email);

        // 2. Read email and extract code
        String emailBody = mailpit.waitForEmailBody(email, 10_000);
        String code = mailpit.extractVerificationCode(emailBody);

        // 3. Verify
        webTestClient.post()
                .uri(BASE + "/verify-code")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"userId": "%s", "code": "%s"}
                        """.formatted(userId, code))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().doesNotExist("Set-Cookie")
                .expectBody()
                .jsonPath("$.userId").isEqualTo(userId);
    }

    @Test
    @DisplayName("verify-code - wrong code returns 4xx")
    void verifyCode_WrongCode_Returns4xx() throws Exception {
        String userId = doRegister(uniqueEmail());

        webTestClient.post()
                .uri(BASE + "/verify-code")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"userId": "%s", "code": "000000"}
                        """.formatted(userId))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    // -------------------------------------------------------------------------
    // Login
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("login - active account returns 200 with JWT cookie")
    void login_ActiveAccount_Returns200WithCookie() {
        String email = uniqueEmail();
        registerAndVerify(email);

        webTestClient.post()
                .uri(BASE + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"username": "%s", "password": "%s"}
                        """.formatted(email, PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("Set-Cookie");
    }

    @Test
    @DisplayName("login - JWT cookie has expected security attributes")
    void login_JwtCookieContract_IsCorrect() {
        String email = uniqueEmail();
        registerAndVerify(email);

        ResponseCookie jwtCookie = loginAndGetJwtCookie(email);

        assertEquals("jwt", jwtCookie.getName());
        assertTrue(jwtCookie.isHttpOnly());
        assertEquals("/", jwtCookie.getPath());
        assertTrue(jwtCookie.getMaxAge().getSeconds() > 0);
    }

    @Test
    @DisplayName("login - unverified account returns 4xx")
    void login_UnverifiedAccount_Returns4xx() throws Exception {
        String email = uniqueEmail();
        doRegister(email);

        webTestClient.post()
                .uri(BASE + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"username": "%s", "password": "%s"}
                        """.formatted(email, PASSWORD))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    @DisplayName("login - wrong credentials return 4xx")
    void login_WrongCredentials_Returns4xx() {
        webTestClient.post()
                .uri(BASE + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"username": "nobody@example.com", "password": "WrongPass1@"}
                        """)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    // -------------------------------------------------------------------------
    // Refresh-code
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("refresh-code - immediately after registration returns 400 due to cooldown")
    void refreshCode_TooSoon_Returns400() {
        String email = uniqueEmail();
        String userId = doRegister(email);

        webTestClient.get()
                .uri(BASE + "/refresh-code?userId=" + userId)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("refresh-code - without auth and without userId returns 400")
    void refreshCode_WithoutAuthAndWithoutUserId_Returns400() {
        webTestClient.get()
                .uri(BASE + "/refresh-code")
                .exchange()
                .expectStatus().isBadRequest();
    }

    // -------------------------------------------------------------------------
    // Logout
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("logout - without JWT cookie returns 401")
    void logout_WithoutJwtCookie_Returns401() {
        webTestClient.post()
                .uri(BASE + "/logout")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("logout - authenticated user returns 204 and clears JWT cookie")
    void logout_AuthenticatedUser_Returns204AndClearsCookie() {
        String email = uniqueEmail();
        registerAndVerify(email);
        ResponseCookie jwtCookie = loginAndGetJwtCookie(email);

        EntityExchangeResult<byte[]> result = webTestClient.post()
                .uri(BASE + "/logout")
                .cookie("jwt", jwtCookie.getValue())
                .exchange()
                .expectStatus().isNoContent()
                .expectCookie().exists("jwt")
                .expectBody()
                .returnResult();

        ResponseCookie clearedCookie = result.getResponseCookies().getFirst("jwt");
        assertNotNull(clearedCookie, "JWT cookie should be present in logout response");
        assertEquals("jwt", clearedCookie.getName());
        assertTrue(clearedCookie.isHttpOnly());
        assertEquals("/", clearedCookie.getPath());
        assertEquals(0, clearedCookie.getMaxAge().getSeconds());

        webTestClient.get()
                .uri("/user")
                .cookie("jwt", jwtCookie.getValue())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("logout - revoking one session does not revoke another session")
    void logout_OneSessionDoesNotRevokeAnotherSession() {
        String email = uniqueEmail();
        registerAndVerify(email);

        ResponseCookie firstSession = loginAndGetJwtCookie(email);
        ResponseCookie secondSession = loginAndGetJwtCookie(email);

        webTestClient.get()
                .uri("/user")
                .cookie("jwt", firstSession.getValue())
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/user")
                .cookie("jwt", secondSession.getValue())
                .exchange()
                .expectStatus().isOk();

        webTestClient.post()
                .uri(BASE + "/logout")
                .cookie("jwt", firstSession.getValue())
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri("/user")
                .cookie("jwt", firstSession.getValue())
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.get()
                .uri("/user")
                .cookie("jwt", secondSession.getValue())
                .exchange()
                .expectStatus().isOk();
    }

    // -------------------------------------------------------------------------
    // JWT / protected endpoints
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("protected endpoint - without JWT cookie returns 401")
    void protectedEndpoint_WithoutJwtCookie_Returns401() {
        webTestClient.get()
                .uri("/user")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("protected endpoint - valid JWT cookie returns 200 with profile")
    void protectedEndpoint_WithValidJwtCookie_Returns200() {
        String email = uniqueEmail();
        registerAndVerify(email);
        ResponseCookie jwtCookie = loginAndGetJwtCookie(email);

        webTestClient.get()
                .uri("/user")
                .cookie("jwt", jwtCookie.getValue())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.user.email").isEqualTo(email);
    }

    @Test
    @DisplayName("protected endpoint - wrong cookie name returns 401")
    void protectedEndpoint_WrongCookieName_Returns401() {
        String email = uniqueEmail();
        registerAndVerify(email);
        ResponseCookie jwtCookie = loginAndGetJwtCookie(email);

        webTestClient.get()
                .uri("/user")
                .cookie("JWT", jwtCookie.getValue())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("protected endpoint - tampered JWT returns 401")
    void protectedEndpoint_TamperedJwt_Returns401() {
        String email = uniqueEmail();
        registerAndVerify(email);
        ResponseCookie jwtCookie = loginAndGetJwtCookie(email);
        String tamperedJwt = tamperToken(jwtCookie.getValue());

        webTestClient.get()
                .uri("/user")
                .cookie("jwt", tamperedJwt)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("protected endpoint - valid JWT for deleted user returns 500 with not-found detail")
    void protectedEndpoint_DeletedUserWithStillValidJwt_Returns500WithNotFoundDetail() {
        String email = uniqueEmail();
        registerAndVerify(email);
        ResponseCookie jwtCookie = loginAndGetJwtCookie(email);

        webTestClient.delete()
                .uri("/user")
                .cookie("jwt", jwtCookie.getValue())
                .exchange()
                .expectStatus().isNoContent();

        // The JWT is still structurally valid but resolver cannot load profile anymore.
        webTestClient.get()
                .uri("/user")
                .cookie("jwt", jwtCookie.getValue())
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .value(error -> assertTrue(error.contains("Profile not found for user ID")));
    }

    // -------------------------------------------------------------------------
    // Verify-code edge cases
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("verify-code - without auth and without userId returns 400")
    void verifyCode_WithoutAuthAndWithoutUserId_Returns400() {
        webTestClient.post()
                .uri(BASE + "/verify-code")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"code": "123456"}
                        """)
                .exchange()
                .expectStatus().isBadRequest();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Registers a user and returns its userId.
     */
    private String doRegister(String email) {
        String[] userId = new String[1];
        webTestClient.post()
                .uri(BASE + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"username": "%s", "password": "%s", "confirmPassword": "%s"}
                        """.formatted(email, PASSWORD, PASSWORD))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.userId").value(id -> userId[0] = (String) id);
        return userId[0];
    }

    /**
     * Logs in and returns the JWT response cookie.
     */
    private ResponseCookie loginAndGetJwtCookie(String email) {
        EntityExchangeResult<byte[]> result = webTestClient.post()
                .uri(BASE + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"username": "%s", "password": "%s"}
                        """.formatted(email, PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectCookie().exists("jwt")
                .expectBody()
                .returnResult();

        ResponseCookie jwtCookie = result.getResponseCookies().getFirst("jwt");
        assertNotNull(jwtCookie, "JWT cookie should be present after login");
        return jwtCookie;
    }

    private String tamperToken(String token) {
        assertNotNull(token);
        assertFalse(token.isBlank());

        char last = token.charAt(token.length() - 1);
        char replacement = (last == 'a') ? 'b' : 'a';
        return token.substring(0, token.length() - 1) + replacement;
    }

    /**
     * Registers a user, reads the email, and verifies the code.
     * Returns the already active userId.
     */
    private String registerAndVerify(String email) {
        try {
            // 1. Register
            String[] userId = new String[1];
            webTestClient.post()
                    .uri(BASE + "/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("""
                            {"username": "%s", "password": "%s", "confirmPassword": "%s"}
                            """.formatted(email, PASSWORD, PASSWORD))
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.userId").value(id -> userId[0] = (String) id);

            // 2. Read code from email
            String emailBody = mailpit.waitForEmailBody(email, 10_000);
            String code = mailpit.extractVerificationCode(emailBody);
            mailpit.deleteAllMessages();

            // 3. Verify
            webTestClient.post()
                    .uri(BASE + "/verify-code")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("""
                            {"userId": "%s", "code": "%s"}
                            """.formatted(userId[0], code))
                    .exchange()
                    .expectStatus().isCreated();

            return userId[0];
        } catch (Exception e) {
            throw new RuntimeException("registerAndVerify failed", e);
        }
    }
}
