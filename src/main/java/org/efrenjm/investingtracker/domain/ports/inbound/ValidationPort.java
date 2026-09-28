package org.efrenjm.investingtracker.domain.ports.inbound;

public interface ValidationPort {
    boolean isValidEmail(String possibleEmail);

    boolean isValidPhone(String possiblePhone);

    boolean isValidPassword(String password);
}
