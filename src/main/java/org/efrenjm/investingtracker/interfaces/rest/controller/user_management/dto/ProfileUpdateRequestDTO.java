package org.efrenjm.investingtracker.interfaces.rest.controller.user_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.efrenjm.investingtracker.domain.dto.ProfileUpdateCommand;
import org.efrenjm.investingtracker.domain.model.user.UserPreferences;

public record ProfileUpdateRequestDTO(
        @NotNull(message = "username cannot be null")
                @NotBlank(message = "username cannot be empty")
                String username,
        @NotNull(message = "firstName cannot be null")
                @NotBlank(message = "firstName cannot be empty")
                String firstName,
        @NotNull(message = "middleName cannot be null")
                @NotBlank(message = "middleName cannot be empty")
                String middleName,
        @NotNull(message = "lastName cannot be null")
                @NotBlank(message = "lastName cannot be empty")
                String lastName,
        String profilePicture,
        UserPreferences userPreferences) {
    /** Converts this REST DTO to a domain command. */
    public ProfileUpdateCommand toCommand() {
        return new ProfileUpdateCommand(
                username, firstName, middleName, lastName, profilePicture, userPreferences);
    }
}
