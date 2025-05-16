package org.efrenjm.investingtracker.domain.model.user;

import lombok.*;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.user.exceptions.CodeExpiredException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.CodeRefreshDisabledException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.InvalidCodeException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.NoVerificationInProcessException;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class User {
	@Builder.Default
	private String id = new ObjectId().toString();
	private String username;
	private String email;
	private String phoneNumber;
	private String password;
	@Builder.Default
	private boolean active = false;
	private VerificationRequest verificationRequest;
	@Builder.Default
	private Set<GrantedAuthority> roles = new HashSet<>();
	private String firstName;
	private String middleName;
	private String lastName;
	private String profilePicture;
	@Builder.Default
	private List<String> wallets = new ArrayList<>();
	private Date createdAt;
	private Date updatedAt;
	private Date lastLogin;
	@Builder.Default
	private List<String> friends = new ArrayList<>();

	public Optional<VerificationRequest> getVerificationRequest() {
		return Optional.ofNullable(this.verificationRequest);
	}

	public boolean isEnabled() {
		return active;
	}

	public boolean isNewUser() {
		return email == null && phoneNumber == null;
	}

	public void validateCode(String code) {
		VerificationRequest req = this.getVerificationRequest()
				.orElseThrow(NoVerificationInProcessException::new);

		if (!req.getCode().equals(code)) {
			throw new InvalidCodeException();
		}

		if (req.getExpiration().before(new Date())) {
			throw new CodeExpiredException();
		}
	}

	public VerificationRequest createVerificationRequest(CodeUsage codeUsage, String credential) {
		this.verificationRequest = VerificationRequest.create(codeUsage, credential);
		return this.verificationRequest;
	}

	public void clearVerificationRequest() {
		this.verificationRequest = null;
	}

	public VerificationRequest refreshVerificationRequest() {
		VerificationRequest req = this.getVerificationRequest()
				.orElseThrow(NoVerificationInProcessException::new);

		if (req.getRefreshPause().after(new Date())) {
			throw new CodeRefreshDisabledException();
		}
		return createVerificationRequest(req.getCodeUsage(), req.getCredential());
	}

	public void completeVerificationRequest () {
		VerificationRequest req = this.getVerificationRequest()
				.orElseThrow(NoVerificationInProcessException::new);

		switch (req.getCodeUsage()) {
			case EMAIL_VERIFICATION -> this.email = req.getCredential();
			case PHONE_VERIFICATION -> this.phoneNumber = req.getCredential();
			case PASSWORD_RESET -> this.password = req.getCredential();
		}
		clearVerificationRequest();
	}
}
