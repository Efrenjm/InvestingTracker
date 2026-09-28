package org.efrenjm.investingtracker.domain.ports.outbound.utils;

public interface ValidationPort {
    boolean isValidEmail(String possibleEmail);

    boolean isValidPhone(String possiblePhone);
}
