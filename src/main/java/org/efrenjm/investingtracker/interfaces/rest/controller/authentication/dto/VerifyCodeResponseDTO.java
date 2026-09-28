package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.efrenjm.investingtracker.domain.model.user.User;

@Schema(description = "Response returned after successful verification of a code.")
public record VerifyCodeResponseDTO(
        @Schema(
                        description = "Identifier of the verified user.",
                        example = "67d2f18d8b17c24e3fe46ed1",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                String userId,
        @Schema(
                        description =
                                "Verified credential (phone or email) associated with the account.",
                        example = "john.doe@email.com",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                String username) {
    public static VerifyCodeResponseDTO from(User user) {
        String verifiedUsername =
                user.getPhoneNumber() != null ? user.getPhoneNumber() : user.getEmail();
        return new VerifyCodeResponseDTO(user.getId(), verifiedUsername);
    }
}
