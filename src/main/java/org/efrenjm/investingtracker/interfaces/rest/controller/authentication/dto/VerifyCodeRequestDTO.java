package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Payload used to validate a one-time verification code.")
public class VerifyCodeRequestDTO {
    @Schema(
            description =
                    "User identifier. Optional for authenticated users; required for anonymous verification flows.",
            example = "67d2f18d8b17c24e3fe46ed1",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userId;

    @Schema(
            description = "6-character verification code sent to the user.",
            example = "A1B2C3",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 6,
            maxLength = 6,
            pattern = "^[A-Z0-9]{6}$")
    @NotNull(message = "A valid code must be provided")
    @NotBlank(message = "Code cannot be blank")
    private String code;
}
