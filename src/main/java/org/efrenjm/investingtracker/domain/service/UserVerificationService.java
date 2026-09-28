package org.efrenjm.investingtracker.domain.service;

import java.util.Date;
import java.util.Random;
import org.efrenjm.investingtracker.domain.model.user.CodeUsage;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.user.VerificationRequest;
import org.efrenjm.investingtracker.domain.model.user.exceptions.CodeExpiredException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.CodeRefreshDisabledException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.InvalidCodeException;
import org.efrenjm.investingtracker.domain.model.user.exceptions.NoVerificationInProcessException;
import org.springframework.stereotype.Service;

@Service
public class UserVerificationService {
    private static final int CODE_EXPIRATION = 10 * 60 * 1000;
    private static final int CODE_REFRESH_DELAY = 60 * 1000;
    private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final Random RANDOM = new Random();

    public VerificationRequest createRequest(CodeUsage codeUsage, String credential) {
        Date now = new Date();
        String newCode = generateCode();

        return VerificationRequest.builder()
                .code(newCode)
                .codeUsage(codeUsage)
                .credential(credential)
                .expiration(new Date(now.getTime() + UserVerificationService.CODE_EXPIRATION))
                .refreshPause(new Date(now.getTime() + UserVerificationService.CODE_REFRESH_DELAY))
                .build();
    }

    public VerificationRequest refreshRequest(VerificationRequest request) {
        if (!request.isRefreshable()) {
            throw new CodeRefreshDisabledException();
        }

        return createRequest(request.getCodeUsage(), request.getCredential());
    }

    public void validateRequest(VerificationRequest request, String code) {
        if (!request.getCode().equals(code)) {
            throw new InvalidCodeException();
        }

        if (request.isExpired()) {
            throw new CodeExpiredException();
        }
    }

    public void completeRequest(User user) {
        VerificationRequest request =
                user.getVerificationRequest().orElseThrow(NoVerificationInProcessException::new);

        String credential = request.getCredential();

        switch (request.getCodeUsage()) {
            case EMAIL_VERIFICATION -> user.setEmail(credential);
            case PHONE_VERIFICATION -> user.setPhoneNumber(credential);
            case PASSWORD_RESET -> user.setPassword(credential);
        }

        user.clearVerificationRequest();
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
}
