package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request payload used to authenticate a user with username and password.")
public class UserPasswordDTO {
    @Schema(
            description = "Username, email, or phone number used for authentication.",
            example = "john.doe@email.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Username must be provided")
    @NotBlank(message = "Username can't be empty")
    private String username;

    @Schema(
            description = "Raw password for login or registration.",
            example = "Str0ngP@ss!",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 8,
            pattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!?])(?=\\S+$).{8,}$")
    @NotNull(message = "Password must be provided")
    @NotBlank(message = "Password can't be empty")
    private String password;
}
