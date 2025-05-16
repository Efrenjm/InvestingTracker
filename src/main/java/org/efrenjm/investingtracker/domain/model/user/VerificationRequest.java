package org.efrenjm.investingtracker.domain.model.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.Random;

@Builder
@Getter
@Setter
@ToString
public class VerificationRequest {
	private static final int CODE_EXPIRATION = 10 * 60 * 1000;
	private static final int CODE_REFRESH_DELAY = 60 * 1000;
	private static final String ALLOWED_CHARACTERS = "AB1CD2EF3GH4IJ5KL6MN7OP8QR9STUVWXYZ";
	private static final int CODE_LENGTH = 6;
	private static final Random RANDOM = new Random();

	private String code;
	private CodeUsage codeUsage;
	private String credential;
	private Date expiration;
	private Date refreshPause;

	public boolean isExpired() {
		return new Date().after(expiration);
	}


	private static String generateCode() {
		StringBuilder code = new StringBuilder(CODE_LENGTH);

		for (int i = 0; i < CODE_LENGTH; i++) {
			int randomIndex = RANDOM.nextInt(ALLOWED_CHARACTERS.length());
			char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
			code.append(randomChar);
		}

		return code.toString();
	}

	public static VerificationRequest create(CodeUsage codeUsage, String credential) {
		Date now = new Date();
		String newCode = generateCode();

		return VerificationRequest.builder()
				.code(newCode)
				.codeUsage(codeUsage)
				.credential(credential)
				.expiration(new Date(now.getTime() + VerificationRequest.CODE_EXPIRATION))
				.refreshPause(new Date(now.getTime() + VerificationRequest.CODE_REFRESH_DELAY))
				.build();
	}
}
