package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.efrenjm.investingtracker.application.service.authentication.exceptions.DefaultRegistrationException;
import org.efrenjm.investingtracker.domain.model.user.User;

@Schema(description = "Response returned after successful user registration.")
public record RegisterResponseDTO(
        @Schema(
                description = "Identifier of the registered user.",
                example = "67d2f18d8b17c24e3fe46ed1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String userId,

        @Schema(
                description = "Credential where the verification code was sent.",
                example = "john.doe@email.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String username,

        @Schema(
                description = "Friendly message shown after the registration request is accepted.",
                example = "You’re almost there! Check your inbox for the next steps.",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String message
) {
    public static final String GENERIC_REGISTRATION_MESSAGE =
            "You’re almost there! Check your inbox for the next steps.";

    public static RegisterResponseDTO from(User user) {
        return user.getVerificationRequest()
                .map(request -> new RegisterResponseDTO(user.getId(), request.getCredential(), GENERIC_REGISTRATION_MESSAGE))
                .orElseThrow(DefaultRegistrationException::new);
    }
}
