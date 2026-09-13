package org.efrenjm.investingtracker.dto.controller.authentication;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class VerifyCodeRequestDTO {
	@NotNull
	private ObjectId userId;
	@NotNull
	private String token;
}
