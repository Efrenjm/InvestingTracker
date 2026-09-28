package org.efrenjm.investingtracker.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.efrenjm.investingtracker.application.service.security.SecurityService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

class SecurityConfigTest {

    private final SecurityService securityService = mock(SecurityService.class);

    @Test
    void corsConfigurationSourceWithoutAllowedOriginsThrowsException() {
        CorsProperties props = new CorsProperties();
        props.setEnabled(true);
        props.setAllowCredentials(true);
        props.setAllowedOrigins(null);

        SecurityConfig config = new SecurityConfig(securityService, props);

        assertThrows(IllegalStateException.class, config::corsConfigurationSource);
    }

    @Test
    void corsConfigurationSourceWithWildcardAndCredentialsThrowsException() {
        CorsProperties props = new CorsProperties();
        props.setEnabled(true);
        props.setAllowCredentials(true);
        props.setAllowedOrigins(List.of("*"));

        SecurityConfig config = new SecurityConfig(securityService, props);

        assertThrows(IllegalStateException.class, config::corsConfigurationSource);
    }

    @Test
    void corsConfigurationSourceWithExplicitOriginsReturnsConfiguredCors() {
        CorsProperties props = new CorsProperties();
        props.setEnabled(true);
        props.setAllowCredentials(true);
        props.setAllowedOrigins(List.of("  http://localhost:3000  ", " "));

        SecurityConfig config = new SecurityConfig(securityService, props);
        CorsConfigurationSource source = config.corsConfigurationSource();

        MockServerHttpRequest request = MockServerHttpRequest.options("/wallets").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        CorsConfiguration cors = source.getCorsConfiguration(exchange);

        assertNotNull(cors);
        assertEquals(List.of("http://localhost:3000"), cors.getAllowedOrigins());
        assertEquals(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"),
                cors.getAllowedMethods());
        assertEquals(true, cors.getAllowCredentials());
    }
}
