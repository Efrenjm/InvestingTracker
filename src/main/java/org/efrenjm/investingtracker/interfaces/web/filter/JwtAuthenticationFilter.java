package org.efrenjm.investingtracker.interfaces.web.filter;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.application.security.port.in.JwtAuthenticationUseCase;
import org.efrenjm.investingtracker.application.security.port.in.SecuritySessionUseCase;
import org.efrenjm.investingtracker.domain.dto.UserIdentity;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;
import org.springframework.http.HttpCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {
    private final JwtAuthenticationUseCase jwtAuthenticationUseCase;
    private final SecuritySessionUseCase sessionUseCase;

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String token = getTokenFromCookie(exchange);

        return Mono.defer(
                () -> {
                    if (!StringUtils.hasText(token)
                            || !jwtAuthenticationUseCase.isValidToken(token)) {
                        return chain.filter(exchange);
                    }

                    String sessionId;
                    try {
                        sessionId = jwtAuthenticationUseCase.extractSessionId(token);
                    } catch (RuntimeException invalidToken) {
                        return chain.filter(exchange);
                    }

                    return sessionUseCase
                            .isSessionActive(sessionId)
                            .onErrorReturn(false)
                            .flatMap(
                                    active -> {
                                        if (!active) {
                                            return chain.filter(exchange);
                                        }

                                        try {
                                            String userId =
                                                    jwtAuthenticationUseCase.extractUserId(token);
                                            Set<SystemRole> roles =
                                                    jwtAuthenticationUseCase.extractRoles(token);
                                            Set<GrantedAuthority> authorities =
                                                    Optional.ofNullable(roles)
                                                            .orElse(new HashSet<>())
                                                            .stream()
                                                            .map(
                                                                    role ->
                                                                            new SimpleGrantedAuthority(
                                                                                    role.name()))
                                                            .collect(Collectors.toSet());
                                            UserIdentity identity = new UserIdentity(userId, roles);
                                            exchange.getAttributes().put("authUser", identity);
                                            UsernamePasswordAuthenticationToken auth =
                                                    new UsernamePasswordAuthenticationToken(
                                                            identity, null, authorities);
                                            return chain.filter(exchange)
                                                    .contextWrite(
                                                            ReactiveSecurityContextHolder
                                                                    .withAuthentication(auth))
                                                    .contextWrite(
                                                            ctx ->
                                                                    ctx.put(
                                                                            ServerWebExchange.class,
                                                                            exchange));
                                        } catch (RuntimeException invalidClaims) {
                                            return chain.filter(exchange);
                                        }
                                    });
                });
    }

    public String getTokenFromCookie(ServerWebExchange exchange) {
        HttpCookie cookie = exchange.getRequest().getCookies().getFirst("jwt");
        return cookie != null ? cookie.getValue() : null;
    }
}
