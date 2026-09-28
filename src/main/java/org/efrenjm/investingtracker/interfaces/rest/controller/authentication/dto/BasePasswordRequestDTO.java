package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Base payload that carries password replacement fields.")
public abstract class BasePasswordRequestDTO {
    @Schema(
            description = "New password to be set for the account.",
            example = "N3wStr0ngP@ss!",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 8,
            pattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!?])(?=\\S+$).{8,}$")
    @NotNull(message = "New password can't be null")
    @NotBlank(message = "New password can't be empty")
    private String newPassword;

    @Schema(
            description = "Password confirmation. Must match newPassword.",
            example = "N3wStr0ngP@ss!",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 8,
            pattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!?])(?=\\S+$).{8,}$")
    @NotNull(message = "Old password can't be null")
    @NotBlank(message = "Old password can't be empty")
    private String confirmPassword;

    @AssertTrue(message = "Passwords doesn't match")
    public boolean arePasswordsMatching() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
}
