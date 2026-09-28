package org.efrenjm.investingtracker.infrastructure.config;

import org.efrenjm.investingtracker.domain.ports.outbound.utils.IdGeneratorPort;
import org.efrenjm.investingtracker.infrastructure.utils.MongoIdGenerator;
import org.efrenjm.investingtracker.infrastructure.utils.UuidIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class IdGeneratorConfig {
    @Bean
    @Primary
    @Profile("!test")
    public IdGeneratorPort defaultIdGenerator() {
        return new MongoIdGenerator();
    }

    @Bean
    @Primary
    @Profile("test")
    public IdGeneratorPort testIdGenerator() {
        return new UuidIdGenerator();
    }
}
