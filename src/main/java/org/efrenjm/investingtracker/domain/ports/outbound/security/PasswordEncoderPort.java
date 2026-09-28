package org.efrenjm.investingtracker.domain.ports.outbound.security;

public interface PasswordEncoderPort {
    String encode(String text);

    boolean matches(String rawPassword, String encodedPassword);
}
