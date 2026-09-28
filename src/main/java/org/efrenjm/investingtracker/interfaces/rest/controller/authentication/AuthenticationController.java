package org.efrenjm.investingtracker.interfaces.rest.controller.authentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.AllArgsConstructor;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.model.user.CodeUsage;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.ports.inbound.AuthPort;
import org.efrenjm.investingtracker.interfaces.annotations.AuthUser;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.AuthResponseDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.ForgotPasswordRequestDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.RegisterRequestDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.RegisterResponseDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.UpdatePasswordRequestDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.UserPasswordDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.VerificationCodeResponseDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.VerifyCodeRequestDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto.VerifyCodeResponseDTO;
import org.efrenjm.investingtracker.interfaces.rest.controller.authentication.exception.NoUserProvidedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Tag(name = "Authentication", description = "Authentication and verification endpoints")
@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthPort authenticationService;

    @Operation(
            summary = "Login with username and password",
            description = "Authenticates the user and sets a JWT cookie in the HTTP response.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Login successful; JWT cookie set",
                headers = {
                    @Header(
                            name = "Set-Cookie",
                            description = "HTTP-only cookie named jwt containing the access token")
                }),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid request body",
                content = @Content(mediaType = "application/problem+json")),
        @ApiResponse(
                responseCode = "401",
                description = "Invalid credentials",
                content = @Content(mediaType = "application/problem+json"))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Credentials used to authenticate a user.",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserPasswordDTO.class),
                            examples = {
                                @ExampleObject(
                                        name = "Email login",
                                        value =
                                                "{\"username\":\"john.doe@email.com\",\"password\":\"Str0ngP@ss!\"}"),
                                @ExampleObject(
                                        name = "Phone login",
                                        value =
                                                "{\"username\":\"+526611234567\",\"password\":\"Str0ngP@ss!\"}")
                            }))
    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponseDTO>> login(
            @Valid @RequestBody UserPasswordDTO req,
            @Parameter(hidden = true) ServerWebExchange exchange) {
        return authenticationService
                .login(req.getUsername(), req.getPassword(), exchange)
                .map(user -> ResponseEntity.ok(AuthResponseDTO.from(user)));
    }

    @Operation(
            summary = "Logout",
            description =
                    "Ends the current session by clearing JWT cookie and invalidating the cached Redis session.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Logout successful; JWT cookie removed"),
        @ApiResponse(
                responseCode = "401",
                description = "Authentication required",
                content = @Content(mediaType = "application/problem+json"))
    })
    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(
            @Parameter(hidden = true) @AuthUser UserIdentity user,
            @Parameter(hidden = true) ServerWebExchange exchange) {
        return authenticationService
                .logout(user, exchange)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @Operation(
            summary = "Register a new user",
            description =
                    "Accepts a registration request and returns a generic verification context.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "User registered",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = RegisterResponseDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = @Content(mediaType = "application/problem+json"))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Registration data for a new user account.",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterRequestDTO.class),
                            examples =
                                    @ExampleObject(
                                            value =
                                                    "{\"username\":\"john.doe@email.com\",\"password\":\"Str0ngP@ss!\",\"confirmPassword\":\"Str0ngP@ss!\"}")))
    @PostMapping("/register")
    public Mono<ResponseEntity<RegisterResponseDTO>> register(
            @Valid @RequestBody RegisterRequestDTO req) {
        return authenticationService
                .register(req.getUsername(), req.getPassword())
                .map(
                        user ->
                                ResponseEntity.created(URI.create("/verify-code"))
                                        .body(RegisterResponseDTO.from(user)));
    }

    @Operation(
            summary = "Refresh verification code",
            description =
                    "Generates and sends a new verification code. If the caller is authenticated, userId is optional. If the caller is anonymous, userId is required.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Verification code refreshed",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema =
                                        @Schema(
                                                implementation =
                                                        VerificationCodeResponseDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Missing user information or refresh is temporarily disabled",
                content = @Content(mediaType = "application/problem+json")),
        @ApiResponse(
                responseCode = "404",
                description = "User not found",
                content = @Content(mediaType = "application/problem+json"))
    })
    @GetMapping("/refresh-code")
    public Mono<ResponseEntity<VerificationCodeResponseDTO>> refreshVerificationCode(
            @Parameter(
                            description =
                                    "User identifier for anonymous calls. Omit when authenticated.",
                            example = "67d2f18d8b17c24e3fe46ed1")
                    @RequestParam(required = false)
                    String userId,
            @Parameter(hidden = true) @AuthUser User user) {
        Mono<User> strategy;
        if (user == null) {
            if (userId == null) {
                return Mono.error(new NoUserProvidedException());
            }
            strategy = authenticationService.refreshVerificationCode(userId);
        } else {
            strategy = authenticationService.refreshVerificationCode(user);
        }
        return strategy.map(
                res ->
                        ResponseEntity.ok(
                                new VerificationCodeResponseDTO("VERIFICATION_CODE_SENT")));
    }

    @Operation(
            summary = "Verify one-time code",
            description = "Validates a one-time code, activates the pending account.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Code verified successfully.",
                headers = {
                    @Header(name = "Location", description = "URL for login after verification")
                },
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = VerifyCodeResponseDTO.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid or expired code, or bad request payload",
                content = @Content(mediaType = "application/problem+json")),
        @ApiResponse(
                responseCode = "404",
                description = "User not found",
                content = @Content(mediaType = "application/problem+json")),
        @ApiResponse(
                responseCode = "409",
                description = "Account already verified or conflicting state",
                content = @Content(mediaType = "application/problem+json"))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description =
                    "Verification payload containing the one-time code and optionally the userId.",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VerifyCodeRequestDTO.class),
                            examples = {
                                @ExampleObject(
                                        name = "Anonymous verification",
                                        value =
                                                "{\"userId\":\"67d2f18d8b17c24e3fe46ed1\",\"code\":\"A1B2C3\"}"),
                                @ExampleObject(
                                        name = "Authenticated verification",
                                        value = "{\"code\":\"A1B2C3\"}")
                            }))
    @PostMapping("/verify-code")
    public Mono<ResponseEntity<VerifyCodeResponseDTO>> verifyCode(
            @Valid @RequestBody VerifyCodeRequestDTO req,
            @Parameter(hidden = true) @AuthUser User user) {
        Mono<User> strategy;
        if (user == null) {
            String userId = req.getUserId();
            if (userId == null) {
                return Mono.error(new NoUserProvidedException());
            }
            strategy = authenticationService.verifyCode(userId, req.getCode());
        } else {
            strategy = authenticationService.verifyCode(user, req.getCode());
        }

        return strategy.map(
                savedUser ->
                        ResponseEntity.created(URI.create("/password"))
                                .body(VerifyCodeResponseDTO.from(savedUser)));
    }

    @Operation(
            summary = "Update email address",
            description =
                    "Initiates a change of the primary email address. Requires code verification.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Email update initiated; verification code sent"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PutMapping("/email")
    public Mono<ResponseEntity<Void>> updateEmail(
            @RequestBody String newEmail, @Parameter(hidden = true) @AuthUser User user) {
        return authenticationService
                .updateCredential(user, CodeUsage.EMAIL_VERIFICATION, newEmail)
                .thenReturn(ResponseEntity.ok().build());
    }

    @Operation(
            summary = "Update phone number",
            description =
                    "Initiates a change of the primary phone number. Requires code verification.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Phone number update initiated; verification code sent"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PutMapping("/phone")
    public Mono<ResponseEntity<Void>> updatePhone(
            @RequestBody String newPhone, @Parameter(hidden = true) @AuthUser User user) {
        return authenticationService
                .updateCredential(user, CodeUsage.PHONE_VERIFICATION, newPhone)
                .thenReturn(ResponseEntity.ok().build());
    }

    @Operation(
            summary = "Update password",
            description =
                    "Changes the user password. Requires the current password for verification.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Password updated successfully"),
        @ApiResponse(
                responseCode = "400",
                description = "Validation error or incorrect old password"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PutMapping("/password")
    public Mono<ResponseEntity<Void>> updatePassword(
            @Valid @RequestBody UpdatePasswordRequestDTO req,
            @Parameter(hidden = true) @AuthUser User user) {
        return authenticationService
                .updatePassword(user, req.getNewPassword(), req.getOldPassword())
                .thenReturn(ResponseEntity.ok().build());
    }

    @Operation(
            summary = "Forgot password",
            description =
                    "Initiates the password reset flow for a user who forgot their credentials.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Password reset initiated; verification code sent"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/forgot-password")
    public Mono<ResponseEntity<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO req) {
        return authenticationService
                .forgotPassword(req.getUsername(), req.getNewPassword())
                .thenReturn(ResponseEntity.ok().build());
    }
}
