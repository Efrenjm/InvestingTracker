package org.efrenjm.investingtracker.domain.model.user;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class VerificationRequest {
    private String code;
    private CodeUsage codeUsage;
    private String credential;
    private Date expiration;
    private Date refreshPause;

    public boolean isExpired() {
        return new Date().after(expiration);
    }

    public boolean isRefreshable() {
        return new Date().after(refreshPause);
    }
}
