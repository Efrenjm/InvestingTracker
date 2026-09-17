package org.efrenjm.investingtracker.interfaces.rest.controller.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned after a verification code is sent.")
public record VerificationCodeResponseDTO(
        @Schema(description = "Stable result code.", example = "VERIFICATION_CODE_SENT")
        String code
) { }
