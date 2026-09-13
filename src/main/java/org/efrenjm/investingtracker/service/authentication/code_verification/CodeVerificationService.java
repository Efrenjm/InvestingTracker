package org.efrenjm.investingtracker.service.authentication.code_verification;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.exception.authentication.InvalidCodeException;
import org.efrenjm.investingtracker.exception.authentication.TokenExpiredException;
import org.efrenjm.investingtracker.model.user.CodeUsage;
import org.efrenjm.investingtracker.model.user.User;
import org.efrenjm.investingtracker.service.model.user.UserService;
import org.efrenjm.investingtracker.service.utils.EmailService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CodeVerificationService /*implements ICodeVerificationservice*/ {
	private static final int TOKEN_EXPIRATION = 10 * 60 * 1000;
	private static final String ALLOWED_CHARACTERS = "AB1CD2EF3GH4IJ5KL6MN7OP8QR9STUVWXYZ";
	private static final int CODE_LENGTH = 6;

	private final UserService userService;
	private final EmailService emailService;

	public Mono<Boolean> validate(User user, String code) {
		Date now = new Date();

		if (!user.getVerificationCode().equals(code)) {
			return Mono.error(new InvalidCodeException());
		}

		if (user.getCodeExpiration().before(now)) {
			return createRequest(user, user.getCodeUsage())
					.flatMap(result -> Mono.error(new TokenExpiredException()));
		}

		return Mono.just(true);
	}

	public Mono<User> createRequest(User user, CodeUsage codeUsage) {
		Date now = new Date();
		String code = generateVerificationCode();

		user.setVerificationCode(code);
		user.setCodeUsage(codeUsage);
		user.setCodeExpiration(new Date(now.getTime() + TOKEN_EXPIRATION));

		return userService.saveUser(user)
				.doOnSuccess(savedUser -> {
					switch (codeUsage) {
						case EMAIL_VERIFICATION:
							emailService.sendVerificationEmail(savedUser.getUpdateEmailRequest(), savedUser.getVerificationCode());
							break;
						case PHONE_VERIFICATION:
							/* TODO: add sms sender */
							break;
					}
				});
	}

	public static String generateVerificationCode() {
		Random random = new Random();
		StringBuilder code = new StringBuilder(CODE_LENGTH);

		for (int i = 0; i < CODE_LENGTH; i++) {
			int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
			char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
			code.append(randomChar);
		}

		return code.toString();
	}
}
