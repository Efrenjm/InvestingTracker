package org.efrenjm.investingtracker.interfaces.rest.controller.user_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.efrenjm.investingtracker.domain.dto.Profile;

import java.util.Set;
import java.util.stream.Collectors;

@Schema(description = "User profile information as expected by the frontend.")
public record UserWebDTO(
        String id,
        String username,
        String email,
        String phoneNumber,
        String firstName,
        String middleName,
        String lastName,
        String avatarUrl,
        Set<String> roles
) {
    public static UserWebDTO from(Profile profile) {
        return new UserWebDTO(
                profile.id(),
                profile.username(),
                profile.email(),
                profile.phoneNumber(),
                profile.firstName(),
                profile.middleName(),
                profile.lastName(),
                profile.profilePicture(),
                profile.roles().stream().map(Enum::name).collect(Collectors.toSet())
        );
    }
}
