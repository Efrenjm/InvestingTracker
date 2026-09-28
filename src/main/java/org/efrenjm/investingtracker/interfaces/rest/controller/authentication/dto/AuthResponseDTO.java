package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.efrenjm.investingtracker.domain.dto.Profile;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.interfaces.rest.controller.user_management.dto.UserWebDTO;

@Schema(description = "Response containing authenticated user information.")
public record AuthResponseDTO(
        @Schema(description = "The authenticated user profile.") UserWebDTO user) {
    public static AuthResponseDTO from(User user) {
        return new AuthResponseDTO(UserWebDTO.from(Profile.from(user)));
    }
}
