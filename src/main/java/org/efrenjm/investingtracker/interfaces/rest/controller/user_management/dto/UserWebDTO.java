package org.efrenjm.investingtracker.interfaces.rest.controller.user_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import java.util.stream.Collectors;
import org.efrenjm.investingtracker.domain.dto.Profile;

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
        Set<String> roles) {
    public static UserWebDTO from(Profile profile) {
        if (profile == null) {
            return null;
        }
        String username = profile.username();
        if (username == null || username.isBlank()) {
            username = profile.email() != null ? profile.email() : profile.phoneNumber();
        }
        Set<String> roles =
                profile.roles() != null
                        ? profile.roles().stream().map(Enum::name).collect(Collectors.toSet())
                        : Set.of();
        return new UserWebDTO(
                profile.id(),
                username,
                profile.email(),
                profile.phoneNumber(),
                profile.firstName(),
                profile.middleName(),
                profile.lastName(),
                profile.profilePicture(),
                roles);
    }
}
