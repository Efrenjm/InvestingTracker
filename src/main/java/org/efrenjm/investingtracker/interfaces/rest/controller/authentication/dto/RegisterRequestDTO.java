package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(
        description =
                "Request payload used to initiate registration with an email address or phone number.")
public class RegisterRequestDTO {
    @Schema(
            description = "Email address or phone number for registration.",
            example = "john.doe@email.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Username must be provided")
    @NotBlank(message = "Username can't be empty")
    private String username;

    @Schema(
            description = "Password to store for the provisional account.",
            example = "Str0ngP@ss!",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 8,
            pattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!?])(?=\\S+$).{8,}$")
    @NotNull(message = "Password must be provided")
    @NotBlank(message = "Password can't be empty")
    private String password;

    @Schema(
            description = "Confirmation of the registration password.",
            example = "Str0ngP@ss!",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Password confirmation must be provided")
    @NotBlank(message = "Password confirmation can't be empty")
    private String confirmPassword;

    @AssertTrue(message = "Passwords doesn't match")
    public boolean arePasswordsMatching() {
        return password != null && password.equals(confirmPassword);
    }

    public RegisterRequestDTO(String username) {
        this.username = username;
    }
}
