package org.efrenjm.investingtracker.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.application.service.security.SecurityService;
import org.efrenjm.investingtracker.interfaces.web.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig
{
	private static final String DEFAULT_PATH_PATTERN = "/**";
	private static final List<String> DEFAULT_ALLOWED_METHODS = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
	private static final List<String> DEFAULT_ALLOWED_HEADERS = List.of("*");
	private static final List<String> DEFAULT_EXPOSED_HEADERS = List.of("Set-Cookie");
	private static final long DEFAULT_MAX_AGE_SECONDS = 1800L;

	private final SecurityService securityService;
	private final CorsProperties corsProperties;

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http)
	{
		ServerHttpSecurity security = http
				.csrf(ServerHttpSecurity.CsrfSpec::disable)
				.formLogin(ServerHttpSecurity.FormLoginSpec::disable)
				.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);

		if (corsProperties.isEnabled())
		{
			security.cors(cors -> cors.configurationSource(corsConfigurationSource()));
		}
		else
		{
			security.cors(ServerHttpSecurity.CorsSpec::disable);
		}

		return security
				.exceptionHandling(exceptionHandling -> exceptionHandling
						.authenticationEntryPoint((exchange, ex) -> {
							exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
							exchange.getResponse().getHeaders().remove(HttpHeaders.WWW_AUTHENTICATE);
							return exchange.getResponse().setComplete();
						})
						.accessDeniedHandler((exchange, denied) -> {
							exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
							exchange.getResponse().getHeaders().remove(HttpHeaders.WWW_AUTHENTICATE);
							return exchange.getResponse().setComplete();
						})
				)
				.authorizeExchange(exchange -> exchange
						.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.pathMatchers(
								"/auth/login",
								"/auth/register",
								"/auth/refresh-code",
								"/auth/verify-code",
								"/auth/forgot-password",
								"/v3/api-docs/**",
								"/swagger-ui/**",
								"/swagger-ui.html"
						)
						.permitAll()
						.anyExchange()
						.authenticated()
				)
				.addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
				.build();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter()
	{
		return new JwtAuthenticationFilter(securityService);
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource()
	{
		List<String> allowedOrigins = configuredOriginsOrEmpty(corsProperties.getAllowedOrigins());
		if (allowedOrigins.isEmpty())
		{
			throw new IllegalStateException("CORS is enabled but no allowed-origins were configured.");
		}

		if (corsProperties.isAllowCredentials() && allowedOrigins.contains("*"))
		{
			throw new IllegalStateException("When allow-credentials is true, allowed-origins cannot contain '*'.");
		}

		CorsConfiguration config = getCorsConfiguration(allowedOrigins);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration(corsProperties.getPathPattern() != null
				? corsProperties.getPathPattern()
				: DEFAULT_PATH_PATTERN, config);
		return source;
	}

	private CorsConfiguration getCorsConfiguration(List<String> allowedOrigins)
	{
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(allowedOrigins);
		config.setAllowedMethods(orDefault(corsProperties.getAllowedMethods(), DEFAULT_ALLOWED_METHODS));
		config.setAllowedHeaders(orDefault(corsProperties.getAllowedHeaders(), DEFAULT_ALLOWED_HEADERS));
		config.setExposedHeaders(orDefault(corsProperties.getExposedHeaders(), DEFAULT_EXPOSED_HEADERS));
		config.setAllowCredentials(corsProperties.isAllowCredentials());
		config.setMaxAge(corsProperties.getMaxAgeSeconds() != null
				? corsProperties.getMaxAgeSeconds()
				: DEFAULT_MAX_AGE_SECONDS);
		return config;
	}

	private static List<String> orDefault(List<String> values, List<String> fallback)
	{
		return values == null || values.isEmpty() ? fallback : values;
	}

	private static List<String> configuredOriginsOrEmpty(List<String> values)
	{
		if (values == null || values.isEmpty())
		{
			return Collections.emptyList();
		}

		return values.stream()
				.map(String::trim)
				.filter(origin -> !origin.isEmpty())
				.toList();
	}
}
