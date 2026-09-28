package org.efrenjm.investingtracker.infrastructure.utils;

import java.util.UUID;
import org.efrenjm.investingtracker.domain.ports.outbound.utils.IdGeneratorPort;
import org.springframework.stereotype.Component;

@Component("uuidIdGenerator")
public class UuidIdGenerator implements IdGeneratorPort {
    @Override
    public String generateId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public boolean isValidId(String id) {
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
