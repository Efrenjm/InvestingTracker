package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Payload used by users to set or change their password.")
public class UpdatePasswordRequestDTO extends BasePasswordRequestDTO {
    @Schema(
            description =
                    "Current password used to authorize the password change. Optional when setting password for the first time.",
            example = "Curr3ntP@ss!",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String oldPassword;
}
