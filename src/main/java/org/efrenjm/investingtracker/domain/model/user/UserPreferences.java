package org.efrenjm.investingtracker.domain.model.user;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class UserPreferences {
	@Builder.Default
	private boolean isEmailPublic = true;

	@Builder.Default
	private boolean isPhonePublic = true;

	@Builder.Default
	private boolean isNamePublic = true;

	@Builder.Default
	private boolean isProfilePublic = true;
}
