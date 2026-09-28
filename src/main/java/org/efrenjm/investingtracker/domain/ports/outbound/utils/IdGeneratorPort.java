package org.efrenjm.investingtracker.domain.ports.outbound.utils;

public interface IdGeneratorPort {
    String generateId();

    boolean isValidId(String id);
}
