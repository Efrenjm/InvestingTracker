package org.efrenjm.investingtracker.infrastructure.persistence.audit;

import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Configuration for reactive MongoDB auditing.
 * Automatically populates createdAt, updatedAt, createdBy, and updatedBy fields
 * on entities that extend BaseMongoEntity and AuditableMongoEntity.
 */
@Configuration
@EnableReactiveMongoAuditing
public class ReactiveAuditingConfig {

    private static final String AUTH_USER_ATTRIBUTE = "authUser";

    /**
     * Provides the current auditor (user ID) from the reactive context.
     * The auditor is extracted from the UserIdentity stored in the ServerWebExchange attributes
     * by the JwtAuthenticationFilter.
     */
    @Bean
    public ReactiveAuditorAware<String> reactiveAuditorAware() {
        return () -> Mono.deferContextual(contextView -> {
            // Try to get the ServerWebExchange from the reactive context
            if (contextView.hasKey(ServerWebExchange.class)) {
                ServerWebExchange exchange = contextView.get(ServerWebExchange.class);
                Object authUser = exchange.getAttribute(AUTH_USER_ATTRIBUTE);

                if (authUser instanceof UserIdentity userIdentity) {
                    return Mono.justOrEmpty(userIdentity.id());
                }
            }
            // Return empty if no authenticated user is available (e.g., during registration)
            return Mono.empty();
        });
    }
}


